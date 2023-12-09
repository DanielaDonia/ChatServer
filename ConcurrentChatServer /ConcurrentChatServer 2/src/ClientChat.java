import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientChat {
    public static final int portNumber = 8083;
    public static final String IP_Server = "localhost";
    private Socket socket;//client socket
    final private BufferedReader readerServer;
    final private BufferedWriter clientOut;
    final private BufferedReader consoleReader;

    public static void main(String[] args) throws IOException {
        ClientChat clientChat = new ClientChat(portNumber, IP_Server);
        clientChat.start();
    }

    public ClientChat(int portNumber, String IP_Server) throws IOException {
            socket = new Socket(IP_Server, portNumber);
            System.out.println("Connect to: " + socket);
            readerServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        clientOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        consoleReader = new BufferedReader(new InputStreamReader(System.in));

    }

    public void thread(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new ReceiveMessagesFromServer());
    }

    public void start() {
        thread();
        if (socket == null) {
            System.out.println("Socket is not properly initialized. Exiting.");
            return;
        }
        try {
            String result = readerServer.readLine();
            System.out.println(result);
            if (result.contains("Error in communication with server")) {
                socket.close();
                return;
            }
            String messageToServer;
            while ((messageToServer = consoleReader.readLine()) != null) {
                clientOut.write(messageToServer + "\n");
                clientOut.flush();
            }
            socket.close();
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
        }
    }


    private class ReceiveMessagesFromServer implements Runnable {

        @Override
        public void run() {

            try {

                   BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String messageIn;
                while ((messageIn = socketIn.readLine()) != null) {
                    System.out.println(messageIn);
                    if (messageIn == null || messageIn.equals("quit")) {
                        System.out.println("Connection closed");
                        break;
                    }
                }
                            socketIn.close();
                            socket.close();
                        } catch (IOException e) {
                            System.out.println("Error during communication or closing" + e.getMessage());
                        }

                    }


    }

            }



