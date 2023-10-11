import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JLieuTCPServer {
    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(9999);//creates a socket and binds it to port 9999
            //serverSocket = new ServerSocket(0) //creates a socket and binds it to next available port

            while (true) {
                System.out.println("TCP Server waiting for client on port " + serverSocket.getLocalPort() + "...");
                Socket connectionSocket = serverSocket.accept();
                //listens for connection and creates a connection socket for communication
                System.out.println("Just connected server port # " +
                        connectionSocket.getLocalSocketAddress() + " to client port # " +
                        connectionSocket.getRemoteSocketAddress());

                DataInputStream in = new DataInputStream(connectionSocket.getInputStream());

                // Receive file name and size from client
                String fileName = in.readUTF();
                long fileSize = in.readLong();
                System.out.println("RECEIVED: from IPAddress " + connectionSocket.getInetAddress() + " and from port " + connectionSocket.getPort() + " the data: " + fileName);
                

                // Receive file content from client
                byte[] fileBytes = new byte[(int) fileSize];
                in.readFully(fileBytes, 0, fileBytes.length);

                // Compute SHA-256 hash of received file
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(fileBytes);
                byte[] digest = md.digest();
                String receivedHashCode = bytesToHex(digest);

                // Send hash code back to client
                DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
                out.writeUTF(receivedHashCode);
                long fileSizeInBits = fileSize * 8;
                System.out.println("Received File Name: " + fileName);
                System.out.println("Received File Size: " + fileSizeInBits + " bits");
                System.out.println("Received Hash Code: " + receivedHashCode);

                connectionSocket.close(); // Close connection socket after this exchange
                System.out.println();
            }
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
