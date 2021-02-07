import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.interfaces.RSAPublicKey;

public class Server {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            printToConsole("Usage: java Server port");
            System.exit(-1);
        }
        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            printToConsole("Failed parsing port\nUsage: java Server port(number)");
            System.exit(-1);
        }

        ServerSocket socketServer = new ServerSocket(port);
        printToConsole("Socket Server started on port " + port);
        printToConsole("\nWaiting for incoming connections...\n");

        while (true) {
            Socket s = socketServer.accept();
            ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());

            String userId;

            try {
                while ((userId = (String) inputStream.readObject()) != null) {
                    String message = (String) inputStream.readObject();
                    BigInteger signature = (BigInteger) inputStream.readObject();

                    if (isValidSignature(userId, message, signature)) {
                        printToConsole(String.format("%s: %s", userId, message));
                    } else {
                        printToConsole(userId + ": [signature is not verified or user is not recognized]");
                    }
                }

            } catch (Exception e) {
            }
        }
    }

    private static boolean isValidSignature(String userId, String message, BigInteger y) {
        boolean result = false;
        if (y.equals(BigInteger.valueOf(-1))) {
            // should only happen if the client could not get a hold of the users private key
            return result;
        }

        try {
            ObjectInputStream publicKeyStream = new ObjectInputStream(new FileInputStream(userId + ".pub"));
            RSAPublicKey userPublicKey = (RSAPublicKey) publicKeyStream.readObject();
            publicKeyStream.close();

            BigInteger n = userPublicKey.getModulus();
            BigInteger e = userPublicKey.getPublicExponent();

            BigInteger x = y.modPow(e, n);
            BigInteger z = hashString(message);

            result = x.equals(z);
        } catch (Exception e) {
        }

        return result;
    }

    private static BigInteger hashString(String stringToComputeFrom) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedString = digest.digest(stringToComputeFrom.getBytes(StandardCharsets.UTF_8));
        return new BigInteger(1, hashedString); // 1 ensures that it's positive
    }

    private static void printToConsole(String message) {
        System.out.println(message);
    }

}
