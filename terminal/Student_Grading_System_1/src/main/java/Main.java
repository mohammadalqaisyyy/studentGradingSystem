import java.sql.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws SQLException {
        int clientNumber = 0;
        final int PORT = 8000;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT);

            ExecutorService executor = Executors.newCachedThreadPool();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientNumber++;
                System.out.println(clientNumber + " - Accept connection");
                Runnable clientHandler = new ClientHandler(clientSocket, clientNumber);
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        DBConnection.closeConnection();
    }
}
