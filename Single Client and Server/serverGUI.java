import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class serverGUI {
    private JFrame frame;
    private JTextArea textArea;
    private JButton startButton, stopButton;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean isRunning = false;

    private static final int PORT = 12345;

    public serverGUI() {
        frame = new JFrame("Multiclient Server");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);

        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(stopButton);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        
        frame.setVisible(true);

        // Button Actions
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
    }

    private void startServer() {
        threadPool = Executors.newFixedThreadPool(10);
        isRunning = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        textArea.append("Server started on port " + PORT + "\n");

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                while (isRunning) {
                    Socket clientSocket = serverSocket.accept();
                    textArea.append("New client connected: " + clientSocket.getInetAddress() + "\n");
                    threadPool.submit(new ClientHandler(clientSocket));
                }
            } catch (IOException e) {
                textArea.append("Server stopped.\n");
            }
        }).start();
    }

    private void stopServer() {
        isRunning = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            threadPool.shutdown();
            textArea.append("Server stopped.\n");
        } catch (IOException e) {
            textArea.append("Error stopping server.\n");
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                String message;

                while ((message = in.readLine()) != null) {
                    textArea.append("Client: " + message + "\n");
                    out.println("Server Response: " + message.toUpperCase());  // Respond to client
                }
            } catch (IOException e) {
                textArea.append("Client disconnected.\n");
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    textArea.append("Error closing client connection.\n");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(serverGUI::new);
    }
}
