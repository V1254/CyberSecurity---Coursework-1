import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Base64;

public class WannaDecrypt {

    public static void main(String args[]) throws Exception {

        PrivateKey masterRSAPrvKey = readMasterRSAKey();

        byte[] freshRSAPrivateEncrypted = readFromFile("wannacry.key");
        byte[] freshRSAPrivateBytes = RSADecrypt(masterRSAPrvKey, freshRSAPrivateEncrypted);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(freshRSAPrivateBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey freshRSAPrivate = kf.generatePrivate(spec);

        byte[] aesKeyEncrypted = readFromFile("aes.key");
        byte[] aesKeyBytes = RSADecrypt(freshRSAPrivate, aesKeyEncrypted);
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        byte[] ciphertext = readFromFile("test.txt.cry");
        byte[] plaintext = AESDecrypt(ciphertext, aesKey);
        writeToFile(plaintext, "test.txt");

    }

    public static PrivateKey readMasterRSAKey() throws Exception {

        String key = "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";

        byte[] keyBytes = Base64.getDecoder().decode(key);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static void writeToFile(byte[] data, String filename) throws Exception {

        File file = new File(filename);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
    }

    public static byte[] readFromFile(String filename) throws Exception {

        File file = new File(filename);
        byte[] data = Files.readAllBytes(file.toPath());
        return data;
    }

    public static byte[] RSADecrypt(PrivateKey key, byte[] encryptedBytes) throws Exception {
        Cipher rsaInstance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaInstance.init(2, key);
        byte[] decryptedData = rsaInstance.doFinal(encryptedBytes);
        return decryptedData;
    }

    public static byte[] AESDecrypt(byte[] cipherText, SecretKey aesKey) throws Exception {
        Cipher aesInstance = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesInstance.init(2, aesKey, new IvParameterSpec(new byte[16]));
        byte[] decrypted = aesInstance.doFinal(cipherText);
        return decrypted;
    }
}
