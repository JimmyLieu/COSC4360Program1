import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JLieuTCPClient {
    public static void main(String[] args) {
    

        String serverName = "localhost";
        int port = 9999;
        String filePath = args[0];

        File file = new File(filePath);
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(fileBytes);
            byte[] digest = md.digest();
            String clientHashCode = bytesToHex(digest);

            // Create connection to server
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket clientSocket = new Socket(serverName, port);
            System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());

            // Send file name, size, and file content to server
            out.writeUTF(file.getName());
            out.writeLong(file.length());
            out.write(fileBytes, 0, fileBytes.length);

            // Timer starts
            long startTime = System.currentTimeMillis();

            // Receive hash code from server
            String receivedHashCode = in.readUTF();

            //Timer ends
            long endTime = System.currentTimeMillis();

            //calculate throughput
            long fileSize = file.length() * 8; // to convert to bits
            double RTT =(endTime - startTime); // this is in ms
            double throughput = (fileSize / RTT) * 1000 ; 
            if (clientHashCode.equals(receivedHashCode)) {
                System.out.println("Successfully sent.");
            } else {
                System.out.println("Error");
            }
            System.out.println("File name: " + file.getName());
            System.out.println("SHA256 hash: " + clientHashCode);
            System.out.println("File size in bits: " + fileSize + " bits");
            System.out.println("Time taken: " + RTT + " ms");
            System.out.println("Throughput: " +throughput +" Mbps");

            clientSocket.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
