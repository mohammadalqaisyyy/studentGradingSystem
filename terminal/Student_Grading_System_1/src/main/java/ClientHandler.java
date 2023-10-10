import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    DataInputStream inputFromClient;
    DataOutputStream outputToClient;
    int clientNumber;

    public ClientHandler(Socket clientSocket, int clientNumber) throws IOException {
        this.clientSocket = clientSocket;
        this.clientNumber = clientNumber;
        inputFromClient = new DataInputStream(clientSocket.getInputStream());
        outputToClient = new DataOutputStream(clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            outputToClient.writeUTF("------------------ Welcome in Student Grading System ------------------");
            System.out.println(clientNumber + " - wait choice");
            label:
            while (true) {
                outputToClient.writeUTF("Enter choice: \n(1) Login\n(2) Exit");
                String choice = inputFromClient.readUTF();
                switch (choice) {
                    case "1" -> {
                        System.out.println(clientNumber + " - Try to login");
                        String user = inputFromClient.readUTF();
                        String password = inputFromClient.readUTF();

                        System.out.println(clientNumber + " - Check User");
                        int type = User.checkUser(user, password);

                        switch (User.userTypes[type]) {
                            case "Admin" -> {
                                System.out.println(clientNumber + " - it is admin " + user);
                                outputToClient.writeUTF("Admin");
                                new AdminControl(user, password, clientSocket);
                                System.out.println(clientNumber + " - exit");
                                break label;
                            }
                            case "Teacher" -> {
                                System.out.println(clientNumber + " - it is teacher " + user);
                                outputToClient.writeUTF("Teacher");
                                new TeacherControl(user, password, clientSocket);
                                System.out.println(clientNumber + " - exit");
                                break label;
                            }
                            case "Student" -> {
                                System.out.println(clientNumber + " - it is student " + user);
                                outputToClient.writeUTF("Student");
                                new StudentControl(user, password, clientSocket);
                                System.out.println(clientNumber + " - exit");
                                break label;
                            }
                            default -> {
                                System.out.println(clientNumber + " - Failed try, maybe it is not user");
                                outputToClient.writeUTF("Wrong user or password");
                                break label;
                            }
                        }
                    }
                    case "2" -> {
                        System.out.println(clientNumber + " - exit");
                        break label;
                    }
                }
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
