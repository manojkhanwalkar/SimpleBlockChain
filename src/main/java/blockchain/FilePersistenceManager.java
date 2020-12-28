package blockchain;


import org.apache.commons.io.comparator.LastModifiedFileComparator;
import util.CertUtil;
import util.CryptUtil;
import util.JSONUtil;
import util.SymKeyStringTuple;

import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FilePersistenceManager  {

    String certFile = "/home/manoj/IdeaProjects/SimpleBlockChain/src/main/resources/blockrsacertificate.pem";
    String privateFile = "/home/manoj/IdeaProjects/SimpleBlockChain/src/main/resources/blockrsakey.der";



    String persistenceDir ;
    PublicKey publicKey;
    PrivateKey privateKey;
    X509Certificate certificate;


    public FilePersistenceManager(String persistenceDir)
    {
        this.persistenceDir = persistenceDir;
        publicKey =  CryptUtil.getPublicKeyFromCertFile(certFile,"RSA");
        certificate = CertUtil.getCertificate(certFile);
        privateKey =  CryptUtil.getPrivateKeyDerFromFile(privateFile,"RSA");
    }



    public void persist(List<Block> blocks) {

            try {
                String transactionId = blocks.get(0).startTransactionId;
                long time = Long.parseLong(transactionId.split("-")[1]);
                File file = new File(persistenceDir + "-" + time);
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));

                // store one block per line .
                for (Block block : blocks)
                {

                    String str = JSONUtil.toJSON(block);

                    SymKeyStringTuple tuple = CryptUtil.encrypt(str,publicKey);
                    EncryptedBlock encryptedBlock = new EncryptedBlock();
                    encryptedBlock.setEncryptedContents(tuple.message);
                    encryptedBlock.setEncryptedKey(tuple.key);

                    str = JSONUtil.toJSON(encryptedBlock);

                    try {
                        writer.write(str);
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
               writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }



    public Block getLastBlockWritten()
    {


        try {
            File dir = new File(persistenceDir);
            File[] files = dir.listFiles();
            if (files==null || files.length==0) {
                return null;
            }

            Arrays.sort(files, Comparator.comparing(File::lastModified).reversed());

            File file = files[0];

                 BufferedReader reader = new BufferedReader(new FileReader(file));
                 String s = reader.readLine();
                 String lastBlockStr=null;
                 while (s != null) {
                     lastBlockStr = s;
                     s = reader.readLine();
                 }
            EncryptedBlock encryptedBlock = (EncryptedBlock) JSONUtil.fromJSON(lastBlockStr, EncryptedBlock.class);
            String str = CryptUtil.decrypt(encryptedBlock.getEncryptedContents(),encryptedBlock.getEncryptedKey(),privateKey);

            System.out.println(str);

            Block block = (Block) JSONUtil.fromJSON(str, Block.class);

            return block;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private File findFile(File[] files, String transactionId)
    {
        //bcn1-114280296944612
        long txnTime = Long.parseLong(transactionId.split("-")[1]);

        File file = files[0];
        long fileTime = Long.parseLong(file.getName().split("-")[1]);

        if (txnTime<fileTime)
            return null;

        file = files[files.length-1];
        fileTime = Long.parseLong(file.getName().split("-")[1]);
        if (txnTime>fileTime)
            return null;

        File fileToSearch =null;

        for (int i=0;i<files.length;i++)
        {
            file = files[i];
            fileTime = Long.parseLong(file.getName().split("-")[1]);
            if (txnTime>fileTime)
                fileToSearch = file;
            else
                break;

        }
        return fileToSearch;
    }
    public Block getLikelyBlock(String transactionId) {

        try {
            File dir = new File(persistenceDir);
            File[] files = dir.listFiles();
            if (files==null || files.length==0) {
                return null;
            }

            Arrays.sort(files, Comparator.comparing(File::lastModified));

            File file = findFile(files,transactionId);
            if (file==null)
                return null;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            while (s != null) {
                EncryptedBlock encryptedBlock = (EncryptedBlock) JSONUtil.fromJSON(s, EncryptedBlock.class);
                String str = CryptUtil.decrypt(encryptedBlock.getEncryptedContents(),encryptedBlock.getEncryptedKey(),privateKey);
                Block block = (Block) JSONUtil.fromJSON(str, Block.class);
                if (transactionInBlock(block,transactionId))
                    return block;
                s = reader.readLine();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean transactionInBlock(Block block, String transactionId) {

        long txnTime = Long.parseLong(transactionId.split("-")[1]);

        long txnStartInBlock = Long.parseLong(block.startTransactionId.split("-")[1]);
        long txnEndInBlock = Long.parseLong(block.endTransactionId.split("-")[1]);

        if (txnStartInBlock<= txnTime && txnEndInBlock >= txnTime)
            return true;
        else
            return false;

    }
}
