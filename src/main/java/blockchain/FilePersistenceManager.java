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
                    encryptedBlock.setEndTransactionId(block.getEndTransactionId());
                    encryptedBlock.setStartTransactionId(block.getStartTransactionId());

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
        long txnTime = getTimeOfTransaction(transactionId);

        File file = files[0];
        long fileTime = getTimeOfTransaction(file.getName());

        if (txnTime<fileTime)
            return null;

        file = files[files.length-1];
        fileTime = getTimeOfTransaction(file.getName());
        if (txnTime>fileTime)
            return null;

        File fileToSearch =null;

        // the transaction has to be in one of the files and the files are sorted , so use a binary search to locate the file.
        int min =0; int max = files.length-1; int mid = (max-min)/2;
        while(min<=max)
        {
            file = files[mid];
            fileTime = getTimeOfTransaction(file.getName()) ;
            if (txnTime<fileTime)
                max = mid;
            else // (txnTime>=fileTime)
            {
                if (mid==max || getTimeOfTransaction(files[mid+1].getName())>txnTime)
                {
                    fileToSearch= file;
                    break;
                }

                min = mid;
            }

            mid = min + (max-min)/2;


        }

        return fileToSearch;

  /*      for (int i=0;i<files.length;i++)
        {
            file = files[i];
            fileTime = getTimeOfTransaction(file.getName()) ;
            if (txnTime>fileTime)
                fileToSearch = file;
            else
                break;

        }
        return fileToSearch;*/
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

                if (transactionInBlock(encryptedBlock,transactionId)) {
                    String str = CryptUtil.decrypt(encryptedBlock.getEncryptedContents(),encryptedBlock.getEncryptedKey(),privateKey);
                    Block block = (Block) JSONUtil.fromJSON(str, Block.class);
                    return block;
                }
                s = reader.readLine();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private long getTimeOfTransaction(String transactionId)
    {
        return Long.parseLong(transactionId.split("-")[1]);
    }

    private boolean transactionInBlock(EncryptedBlock block, String transactionId) {

        long txnTime = getTimeOfTransaction(transactionId);

        long txnStartInBlock = getTimeOfTransaction(block.startTransactionId);
        long txnEndInBlock = getTimeOfTransaction(block.endTransactionId);

        if (txnStartInBlock<= txnTime && txnEndInBlock >= txnTime)
            return true;
        else
            return false;

    }
}
