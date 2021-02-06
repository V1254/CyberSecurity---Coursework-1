import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: java Client host port userid");
            System.exit(-1);
        }

        String hostString = args[0];
        int port = Integer.parseInt(args[1]);
        String userId = args[2];

        handleServerCommunication(hostString, port, userId);
    }

    private static void handleServerCommunication(String host, int port, String userId) throws Exception {
        Socket socket = new Socket(host, port);
        Scanner scanner = new Scanner(System.in);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

        printToConsole(String.format("Connected to server on %s:%d, with userId %s", host, port, userId));
        while (true) {
            printToConsole("Do you want to send a message? (Y)es/(N)o");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("n") || choice.equalsIgnoreCase("no")) {
                printToConsole("Terminating Connection");
                scanner.close();
                break;
            } else if (choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("yes")) {
                printToConsole("Type your message below and press enter/return when done");
                String message = scanner.nextLine();
                BigInteger x = hashString(message);
                BigInteger y = getUserSignature(userId, x);

                outputStream.writeObject(userId);
                outputStream.writeObject(message);
                outputStream.writeObject(y);
                printToConsole("Finished Sending message to the server\n");
            }
        }

        socket.close();

    }

    private static BigInteger getUserSignature(String userId, BigInteger x) {

        BigInteger y = BigInteger.valueOf(-1);

        try {
            ObjectInputStream privateKeyStream = new ObjectInputStream(new FileInputStream(userId + ".prv"));
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKeyStream.readObject();

            BigInteger n = rsaPrivateKey.getModulus();
            BigInteger d = rsaPrivateKey.getPrivateExponent();

            y = x.modPow(d, n);
            privateKeyStream.close();
        } catch (Exception e) {
            printToConsole("Failed getting user signature, progresssing with fake signature(-1)");
        }

        return y;
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
