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


    // new file everytime we persist = bc + timestamp

    // restore one file at a time , sorted by time .


    public void persist(List<Block> blocks) {

            try {
                File file = new File(persistenceDir + System.currentTimeMillis() + ".data");
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

        int numBlocks=0;


    public BlockChain restore()
    {

        BlockChain blockChain=null;
        try {
            File dir = new File(persistenceDir);
            File[] files = dir.listFiles();
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);


            if (files.length==0) {

                return null;

            }

            blockChain = new BlockChain();
            for (File file : files ) {

                 BufferedReader reader = new BufferedReader(new FileReader(file));
                 String s = reader.readLine();
                 while (s != null) {
                     EncryptedBlock encryptedBlock = (EncryptedBlock) JSONUtil.fromJSON(s, EncryptedBlock.class);
                     String str = CryptUtil.decrypt(encryptedBlock.getEncryptedContents(),encryptedBlock.getEncryptedKey(),privateKey);
                     Block block = (Block) JSONUtil.fromJSON(str, Block.class);
                     blockChain.restore(block);
                     s = reader.readLine();
                     numBlocks++;
                 }

             }

            System.out.println("Number of blocks read are " + numBlocks);



            return blockChain;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return blockChain;


    }







}
