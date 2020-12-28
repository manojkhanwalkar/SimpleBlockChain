package util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import static java.util.Base64.getEncoder;

//https://jamielinux.com/docs/openssl-certificate-authority/sign-server-and-client-certificates.html  - tutorial to generate root , intermediate and leaf certificates.
// openssl pkcs8 -topk8 -inform PEM -outform DER -in private.pem -out private.der -nocrypt

public class CertUtil {


  //  private static final String ALGO = "SHA256withECDSA";



    public static X509Certificate getCertificate(String certFile)
    {
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            FileInputStream is = new FileInputStream(certFile);
            X509Certificate cer = (X509Certificate) fact.generateCertificate(is);


            //System.out.println(cer);
            return cer;
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }



    public static X509Certificate getCertificate(byte[] certBytes)
    {
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");

            ByteArrayInputStream bis = new ByteArrayInputStream(certBytes);

            X509Certificate cer = (X509Certificate) fact.generateCertificate(bis);


            //System.out.println(cer);
            return cer;
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCertAsString(X509Certificate certificate)
    {
        try {
            String certStr = getEncoder().encodeToString(certificate.getEncoded());
            return certStr;
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static X509Certificate getCertFromString(String certStr)
    {
        byte[] bytes =  Base64.getDecoder().decode(certStr.getBytes());

        X509Certificate certificate = getCertificate(bytes);
        return certificate;
    }




 /*   public static byte[] extractPublicKeyFromFile(String fileName)
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

    public static PublicKey getPublicKey(byte[] bytes)
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



    public static PrivateKey loadPrivateKey(String file){
        try {
            File privKeyFile = new File(file);
            // read private key DER file
            DataInputStream dis = new DataInputStream(new FileInputStream(privKeyFile));
            byte[] privKeyBytes = new byte[(int)privKeyFile.length()];
            dis.read(privKeyBytes);
            dis.close();

            KeyFactory kf = KeyFactory.getInstance("EC");
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
*/


    public static void main(String[] args) throws Exception {


 /*       String certFile = "/home/manoj/IdeaProjects/securitytest/src/main/resources/certificate.pem";

        X509Certificate certificate = getCertificate(certFile);

        PublicKey key = certificate.getPublicKey();

        String certStr = getEncoder().encodeToString(certificate.getEncoded());

        byte[] bytes =  Base64.getDecoder().decode(certStr.getBytes());

        X509Certificate certificate1 = getCertificate(bytes);

        System.out.println(certificate1); */



    /*String rootCertFile = "/home/manoj/ca/certs/ca.cert.pem";

        X509Certificate rootCert = getCertificate(rootCertFile);

        System.out.println(rootCert);*/


  /*      String interCertFile = "/home/manoj/ca/intermediate/certs/intermediate.cert.pem";

        X509Certificate interCert = getCertificate(interCertFile);

        System.out.println(interCert); */


/*        String leafCertFile = "/home/manoj/ca/intermediate/certs/www.example.com.cert.pem";

        X509Certificate leafCert = getCertificate(leafCertFile);

        System.out.println(leafCert); */


        String certChainFile = "/home/manoj/ca/intermediate/certs/ca-chain.cert.pem";

        X509Certificate certChainCert = getCertificate(certChainFile);

        System.out.println(certChainCert);







     /*  PublicKey pub =  getPublicKey(extractPublicKeyFromFile("/home/manoj/IdeaProjects/phoenix/src/main/resources/idpcertificate.pem"));
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


        System.out.println(result); */



    }



}
