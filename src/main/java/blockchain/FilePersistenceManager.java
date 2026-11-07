package blockchain;


import util.CertUtil;
import util.CryptUtil;
import util.JSONUtil;
import util.SymKeyStringTuple;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FilePersistenceManager  {

    /** Environment variable (or system property) naming the PEM certificate whose RSA public key encrypts blocks at rest. */
    public static final String CERT_FILE_SETTING = "BC_BLOCK_CERT_FILE";
    /** Environment variable (or system property) naming the PKCS#8 DER RSA private key that decrypts blocks at rest. */
    public static final String PRIVATE_KEY_FILE_SETTING = "BC_BLOCK_KEY_FILE";

    /** Format version written by this code: AES-256-GCM content encryption, RSA-OAEP(SHA-256) key wrap. */
    static final int ENCRYPTION_VERSION = 2;

    String persistenceDir ;
    PublicKey publicKey;
    PrivateKey privateKey;
    X509Certificate certificate;


    public FilePersistenceManager(String persistenceDir)
    {
        this.persistenceDir = persistenceDir;
        String certFile = requiredSetting(CERT_FILE_SETTING);
        String privateFile = requiredSetting(PRIVATE_KEY_FILE_SETTING);
        publicKey =  CryptUtil.getPublicKeyFromCertFile(certFile,"RSA");
        certificate = CertUtil.getCertificate(certFile);
        privateKey =  CryptUtil.getPrivateKeyDerFromFile(privateFile,"RSA");
        if (publicKey == null || privateKey == null) {
            throw new IllegalStateException("Could not load the block encryption key pair from "
                    + CERT_FILE_SETTING + " / " + PRIVATE_KEY_FILE_SETTING);
        }
    }

    private static String requiredSetting(String name)
    {
        String value = System.getenv(name);
        if (value == null || value.isBlank())
            value = System.getProperty(name);
        if (value == null || value.isBlank())
            throw new IllegalStateException(name + " must be set (environment variable or system property)");
        return value;
    }

    private static byte[] associatedData(String startTransactionId, String endTransactionId)
    {
        return (startTransactionId + "|" + endTransactionId).getBytes(StandardCharsets.UTF_8);
    }


    public void persist(List<Block> blocks) {

            String transactionId = blocks.get(0).startTransactionId;
            long time = Long.parseLong(transactionId.split("-")[1]);
            File file = new File(persistenceDir + "-" + time);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

                // store one block per line .
                for (Block block : blocks)
                {

                    String str = JSONUtil.toJSON(block);

                    SymKeyStringTuple tuple;
                    try {
                        tuple = CryptUtil.encryptAuthenticated(str, publicKey,
                                associatedData(block.getStartTransactionId(), block.getEndTransactionId()));
                    } catch (GeneralSecurityException e) {
                        // never fall back to writing the block unencrypted
                        throw new IllegalStateException("Block encryption failed", e);
                    }
                    EncryptedBlock encryptedBlock = new EncryptedBlock();
                    encryptedBlock.setVersion(ENCRYPTION_VERSION);
                    encryptedBlock.setEncryptedContents(tuple.message);
                    encryptedBlock.setEncryptedKey(tuple.key);
                    encryptedBlock.setEndTransactionId(block.getEndTransactionId());
                    encryptedBlock.setStartTransactionId(block.getStartTransactionId());

                    str = JSONUtil.toJSON(encryptedBlock);

                    writer.write(str);
                    writer.newLine();

                }
               writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    /**
     * Decrypts a stored block. Version 2 blocks are authenticated (tampering, or moving a block
     * to another record, fails decryption); blocks without a version use the legacy format.
     */
    private Block decryptBlock(EncryptedBlock encryptedBlock)
    {
        String str;
        if (encryptedBlock.getVersion() != null && encryptedBlock.getVersion() == ENCRYPTION_VERSION) {
            try {
                str = CryptUtil.decryptAuthenticated(encryptedBlock.getEncryptedContents(), encryptedBlock.getEncryptedKey(),
                        privateKey, associatedData(encryptedBlock.getStartTransactionId(), encryptedBlock.getEndTransactionId()));
            } catch (GeneralSecurityException | IllegalArgumentException e) {
                System.err.println("Rejecting block " + encryptedBlock.getStartTransactionId()
                        + ": integrity check failed");
                return null;
            }
        } else if (encryptedBlock.getVersion() == null) {
            str = CryptUtil.decrypt(encryptedBlock.getEncryptedContents(), encryptedBlock.getEncryptedKey(), privateKey);
        } else {
            System.err.println("Unsupported block encryption version " + encryptedBlock.getVersion());
            return null;
        }
        if (str == null)
            return null;
        return (Block) JSONUtil.fromJSON(str, Block.class);
    }



    public Block getLastBlockWritten()
    {


        File[] files = listBlockFiles();
        if (files.length==0) {
            return null;
        }

        File file = files[files.length-1];

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                 String s = reader.readLine();
                 String lastBlockStr=null;
                 while (s != null) {
                     lastBlockStr = s;
                     s = reader.readLine();
                 }
            if (lastBlockStr == null)
                return null;
            EncryptedBlock encryptedBlock = (EncryptedBlock) JSONUtil.fromJSON(lastBlockStr, EncryptedBlock.class);
            if (encryptedBlock == null)
                return null;

            return decryptBlock(encryptedBlock);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /** Block files in the persistence directory, ordered by the start time encoded in their names. */
    private File[] listBlockFiles()
    {
        File dir = new File(persistenceDir);
        File[] files = dir.listFiles(f -> f.isFile() && parseTime(f.getName()) >= 0);
        if (files == null)
            return new File[0];
        Arrays.sort(files, Comparator.comparingLong(f -> parseTime(f.getName())));
        return files;
    }


    /**
     * Returns the last file whose start time is &lt;= the transaction time (files sorted ascending),
     * or null when the transaction predates every file.
     */
    static File findFile(File[] files, long txnTime)
    {
        File fileToSearch = null;
        int min = 0;
        int max = files.length - 1;
        while (min <= max)
        {
            int mid = (min + max) >>> 1;
            long fileTime = parseTime(files[mid].getName());
            if (fileTime <= txnTime) {
                fileToSearch = files[mid];
                min = mid + 1;
            } else {
                max = mid - 1;
            }
        }

        return fileToSearch;
    }
    public Block getLikelyBlock(String transactionId) {

        long txnTime = parseTime(transactionId);
        if (txnTime < 0)
            return null;

        File[] files = listBlockFiles();
        if (files.length==0) {
            return null;
        }

        File file = findFile(files,txnTime);
        if (file==null)
            return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String s = reader.readLine();
            while (s != null) {
                EncryptedBlock encryptedBlock = (EncryptedBlock) JSONUtil.fromJSON(s, EncryptedBlock.class);

                if (encryptedBlock != null && transactionInBlock(encryptedBlock,txnTime)) {
                    return decryptBlock(encryptedBlock);
                }
                s = reader.readLine();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /** Parses the time component of "&lt;name&gt;-&lt;time&gt;"; returns -1 when it is not in that form. */
    static long parseTime(String transactionId)
    {
        if (transactionId == null)
            return -1;
        int idx = transactionId.lastIndexOf('-');
        if (idx < 0 || idx == transactionId.length() - 1)
            return -1;
        try {
            return Long.parseLong(transactionId.substring(idx + 1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean transactionInBlock(EncryptedBlock block, long txnTime) {

        long txnStartInBlock = parseTime(block.startTransactionId);
        long txnEndInBlock = parseTime(block.endTransactionId);

        return txnStartInBlock <= txnTime && txnEndInBlock >= txnTime;

    }
}
