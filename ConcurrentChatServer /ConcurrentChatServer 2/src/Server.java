import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 8083;
    private static Set<Socket> clientSockets = new HashSet<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New user connected: " + clientSocket);
                clientSockets.add(clientSocket);
                executorService.submit(new ClientHandler(clientSocket));
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader reader;
        private String clientName;

        public ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        @Override
        public void run() {
            try {

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                writer.println("Enter your name:");
                clientName = reader.readLine();
                broadcast(clientName + " has joined the chat.");

                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) {
                    broadcast(clientName + ": " + clientMessage);
                    if (clientMessage.equals("quit")) {
                        System.out.println(clientName + " is leaving.");
                        clientSockets.remove(clientSocket);
                        broadcast(clientName + " has left the chat.");
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        private void broadcast(String message) {
            for (Socket socket : clientSockets) {
                try {
                    PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
                    socketWriter.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}