import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TCPClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java TCPClient <server-ip> <file-path>");
            return;
        }

        String serverName = args[0]; // Server hostname or IP address
        int port = 9999; // Port number to connect
        String filePath = args[1]; // File path to send to the server

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("File not found.");
                return;
            }

            // Compute SHA256 hash of the file
            String fileHash = computeSHA256(file);

            // Establish connection to the server
            Socket clientSocket = new Socket(serverName, port);
            System.out.println("Connected to " + clientSocket.getRemoteSocketAddress());

            // Send file to server
            OutputStream outToServer = clientSocket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outToServer);
            objectOutputStream.writeObject(file);
            objectOutputStream.flush();

            // Start timer
            long startTime = System.currentTimeMillis();

            // Receive response from server
            InputStream inFromServer = clientSocket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inFromServer);
            String serverHash = (String) objectInputStream.readObject();

            // Stop timer
            long endTime = System.currentTimeMillis();

            // Calculate throughput
            long fileSize = file.length() * 8; // File size in bits
            double timeTaken = (endTime - startTime); // Time taken for server response in milliseconds
            double throughput = (fileSize / timeTaken) * 1000 / (1024 * 1024); // Throughput in Mbps

            // Print results
            System.out.println("File Name: " + file.getName());
            System.out.println("Client Hash: " + fileHash);
            System.out.println("File Size: " + fileSize + " bits");
            System.out.println("Time taken for server response: " + timeTaken + " ms");
            System.out.println("Throughput: " + String.format("%.2f", throughput) + " Mbps");

            // Check if the received hash code matches the client's computed hash code
            if (serverHash.equals(fileHash)) {
                System.out.println("Successfully sent!");
            } else {
                System.out.println("Error!");
            }

            // Close the socket
            clientSocket.close();
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
            // Handle exceptions, if any
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
