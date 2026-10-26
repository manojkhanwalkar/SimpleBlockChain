package util;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static java.util.Base64.getEncoder;

public class CryptUtil {



    private static byte[] extractPublicKeyFromFile(String fileName)
    {
        try {
            File privKeyFile = new File(fileName);
            // read private key DER file
            DataInputStream dis = new DataInputStream(new FileInputStream(privKeyFile));
            byte[] privKeyBytes = new byte[(int)privKeyFile.length()];
            dis.read(privKeyBytes);
            dis.close();

            return privKeyBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static PublicKey getPublicKey(byte[] bytes)
    {
        try {

            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate cer = (X509Certificate) fact.generateCertificate(bis);
            PublicKey key = cer.getPublicKey();


       //     System.out.println(cer);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String convertPublicKeyToString(PublicKey key)
    {


        // public key to bytes , bytes to public key
        String publicKeyStr= getEncoder().encodeToString(key.getEncoded());
        return publicKeyStr;

    }


    public static String convertPrivateKeyToString(PrivateKey key)
    {



        String privateKeyStr= getEncoder().encodeToString(key.getEncoded());
        return privateKeyStr;

    }


    public static PublicKey convertStringtoPublicKey(String str, String type)
    {
        try{
            byte[] byteKey = Base64.getDecoder().decode(str.getBytes());
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance(type);

            return kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }


    public static PrivateKey convertStringtoPrivateKey(String str, String type)
    {
        try{
            byte[] byteKey = Base64.getDecoder().decode(str.getBytes());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(spec);


           /*
            X509EncodedKeySpec X509privateKey= new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance(type);

            return kf.generatePrivate(X509privateKey); */

           return privKey;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    static SecureRandom secureRandom = new SecureRandom();

    private static final int GCM_IV_BYTES = 12;
    private static final int GCM_TAG_BITS = 128;
    private static final String RSA_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final OAEPParameterSpec OAEP_SHA256 =
            new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);

    /**
     * Hybrid encryption with integrity: a fresh AES-256-GCM key encrypts the message (random 96-bit IV,
     * prepended to the ciphertext, and {@code aad} bound as associated data) and is wrapped with RSA-OAEP(SHA-256).
     */
    public static SymKeyStringTuple encryptAuthenticated(String message, PublicKey key, byte[] aad) throws GeneralSecurityException
    {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        SecretKey secKey = generator.generateKey();

        byte[] iv = new byte[GCM_IV_BYTES];
        secureRandom.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
        if (aad != null)
            cipher.updateAAD(aad);
        byte[] ct = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        byte[] out = new byte[iv.length + ct.length];
        System.arraycopy(iv, 0, out, 0, iv.length);
        System.arraycopy(ct, 0, out, iv.length, ct.length);

        Cipher rsa = Cipher.getInstance(RSA_OAEP);
        rsa.init(Cipher.ENCRYPT_MODE, key, OAEP_SHA256);

        SymKeyStringTuple tuple = new SymKeyStringTuple();
        tuple.key = Base64.getEncoder().encodeToString(rsa.doFinal(secKey.getEncoded()));
        tuple.message = Base64.getEncoder().encodeToString(out);
        return tuple;
    }

    /** Reverses {@link #encryptAuthenticated}; throws if the ciphertext, key or associated data was altered. */
    public static String decryptAuthenticated(String message, String key, PrivateKey privateKey, byte[] aad) throws GeneralSecurityException
    {
        Cipher rsa = Cipher.getInstance(RSA_OAEP);
        rsa.init(Cipher.DECRYPT_MODE, privateKey, OAEP_SHA256);
        byte[] rawKey = rsa.doFinal(Base64.getDecoder().decode(key));

        byte[] in = Base64.getDecoder().decode(message);
        if (in.length < GCM_IV_BYTES + GCM_TAG_BITS / 8)
            throw new AEADBadTagException("ciphertext too short");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(rawKey, "AES"),
                new GCMParameterSpec(GCM_TAG_BITS, in, 0, GCM_IV_BYTES));
        if (aad != null)
            cipher.updateAAD(aad);
        byte[] pt = cipher.doFinal(in, GCM_IV_BYTES, in.length - GCM_IV_BYTES);
        return new String(pt, StandardCharsets.UTF_8);
    }

/*
    public static SymKeyStringTuple encryptWithIV(String message, PublicKey key)
    {
        try {

            byte[] iv = new byte[128/8];
            secureRandom.nextBytes(iv);


            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128); // The AES key size in number of bits
            SecretKey secKey = generator.generateKey();

            Cipher cipher =  Cipher.getInstance("AES");

            IvParameterSpec ivSpec=new IvParameterSpec(Base64.getDecoder().decode(iv));
            cipher.init(Cipher.ENCRYPT_MODE, secKey,ivSpec);
            String encryptedMessage = Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));

            Cipher cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher1.init(Cipher.PUBLIC_KEY, key);
            String encryptedKey = Base64.getEncoder().encodeToString(cipher1.doFinal(secKey.getEncoded()));

            SymKeyStringTuple tuple = new SymKeyStringTuple();
            tuple.key = encryptedKey;
            tuple.message= encryptedMessage;

            return tuple;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }*/


    /**
     * Legacy format only (AES/ECB without integrity, RSA PKCS#1 v1.5 key wrap). Kept so blocks written
     * before the authenticated format can still be read; new data uses {@link #encryptAuthenticated}.
     */
    @Deprecated
    public static String decrypt(String message, String key, PrivateKey privateKey)  {

      try {
          Cipher cipher = Cipher.getInstance("RSA");
          cipher.init(Cipher.DECRYPT_MODE, privateKey);

          byte[] decryptedKey = cipher.doFinal(Base64.getDecoder().decode(key));

          SecretKey originalKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
          Cipher aesCipher = Cipher.getInstance("AES");
          aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
          byte[] bytePlainText = aesCipher.doFinal(Base64.getDecoder().decode(message));
          String plainText = new String(bytePlainText);

          return plainText;
      } catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
      } catch (NoSuchPaddingException e) {
          e.printStackTrace();
      } catch (InvalidKeyException e) {
          e.printStackTrace();
      } catch (IllegalBlockSizeException e) {
          e.printStackTrace();
      } catch (BadPaddingException e) {
          e.printStackTrace();
      }

      return null;

  }




    public static PublicKey getPublicKeyFromCertFile(String file,String keyType)
    {
        return getPublicKey(extractPublicKeyFromFile(file));
    }



    public static PrivateKey getPrivateKeyDerFromFile(String file, String keyType){
        try {
            File privKeyFile = new File(file);
            // read private key DER file
            DataInputStream dis = new DataInputStream(new FileInputStream(privKeyFile));
            byte[] privKeyBytes = new byte[(int)privKeyFile.length()];
            dis.read(privKeyBytes);
            dis.close();

            KeyFactory kf = KeyFactory.getInstance(keyType);
            // decode private key
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privKeyBytes);
            PrivateKey privKey = kf.generatePrivate(privSpec);

            return privKey;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return null;
    }



    public static void main(String[] args) throws Exception {

  /*
     private static final String ALGO = "SHA256withECDSA";

  PublicKey pub =  getPublicKey(extractPublicKeyFromFile("/home/manoj/IdeaProjects/phoenix/src/main/resources/idpcertificate.pem"));
        PrivateKey priv = loadPrivateKey("/home/manoj/IdeaProjects/phoenix/src/main/resources/idpprivatekey.der");

        String plainText = "Hello World from EC Public and Private keys";

        Signature ecdsaSign = Signature.getInstance(ALGO);
        ecdsaSign.initSign(priv);
        ecdsaSign.update(plainText.getBytes("UTF-8"));
        byte[] signature = ecdsaSign.sign();


        Signature ecdsaVerify = Signature.getInstance(ALGO);

        ecdsaVerify.initVerify(pub);
        ecdsaVerify.update(plainText.getBytes("UTF-8"));
        boolean result = ecdsaVerify.verify(signature);


        System.out.println(result);*/



    }



}
