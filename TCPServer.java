import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TCPServer {
    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(9999);
            System.out.println("TCP Server waiting for client on port " + serverSocket.getLocalPort() + "...");

            while (true) {
                Socket connectionSocket = serverSocket.accept();
                System.out.println("Just connected server port # " + connectionSocket.getLocalSocketAddress() +
                        " to client port # " + connectionSocket.getRemoteSocketAddress());

                // Get incoming file from client
                ObjectInputStream objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());
                File receivedFile = (File) objectInputStream.readObject();

                // Compute SHA256 hash of received file
                String receivedFileHash = computeSHA256(receivedFile);

                // Send the hash back to the client
                DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
                out.writeUTF(receivedFileHash);

                // Close connection socket after this exchange
                connectionSocket.close();
                System.out.println("Connection closed.");
                System.out.println();
            }
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Compute SHA256 hash of the file
    private static String computeSHA256(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(file);
        byte[] dataBytes = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(dataBytes)) != -1) {
            digest.update(dataBytes, 0, bytesRead);
        }
        byte[] hashBytes = digest.digest();
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte hashByte : hashBytes) {
            hexStringBuilder.append(String.format("%02x", hashByte));
        }
        fis.close();
        return hexStringBuilder.toString();
    }
}
