import java.io.*;
import java.net.*;

public class client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to server...");
            String message;
            
            while (true) {
                System.out.print("Enter message to send: ");
                message = userInput.readLine();
                out.println(message);

                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }

                String serverResponse = in.readLine();
                System.out.println("Server: " + serverResponse);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
