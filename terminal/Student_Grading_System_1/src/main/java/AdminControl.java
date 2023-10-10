import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

public class AdminControl {
    Admin admin;
    Scanner scanner;
    DataInputStream inputFromClient;
    DataOutputStream outputToClient;

    public AdminControl(String nameAdmin, String passwordAdmin, Socket clientSocket) throws SQLException, IOException {
        admin = new Admin(nameAdmin, passwordAdmin);
        outputToClient = new DataOutputStream(clientSocket.getOutputStream());
        inputFromClient = new DataInputStream(clientSocket.getInputStream());

        scanner = new Scanner(System.in);

        String choice;
        label:
        while (true) {
            outputToClient.writeUTF(
                    """
                            \n
                            1. Add user.
                            2. View teachers.
                            3. View students.
                            4. Add course.
                            5. View courses.
                            6. Enroll student in course.
                            7. Exit.
                            \n"""
            );
            choice = inputFromClient.readUTF();
            outputToClient.writeUTF(String.valueOf(choice));
            switch (choice) {
                case "1" -> {
                    int userType = Integer.parseInt(inputFromClient.readUTF());
                    String name = inputFromClient.readUTF();
                    String email = inputFromClient.readUTF();
                    String password = inputFromClient.readUTF();
                    admin.addUser(userType, name, email, password);
                }
                case "2" -> {
                    StringBuilder str = new StringBuilder();
                    for (Map.Entry<Integer, String> entry : admin.getTeachers().entrySet())
                        str.append("   (").append(entry.getKey()).append(")_").append(entry.getValue()).append("    ");
                    outputToClient.writeUTF(str.toString());
                }
                case "3" -> {
                    StringBuilder str = new StringBuilder();
                    for (Map.Entry<Integer, String> entry : admin.getStudents().entrySet())
                        str.append("   (").append(entry.getKey()).append(")_").append(entry.getValue()).append("    ");
                    outputToClient.writeUTF(str.toString());
                }
                case "4" -> {
                    StringBuilder str = new StringBuilder();
                    for (Map.Entry<Integer, String> entry : admin.getTeachers().entrySet())
                        str.append("   (").append(entry.getKey()).append(")_").append(entry.getValue()).append("    ");
                    outputToClient.writeUTF(str.toString());

                    String courseName = inputFromClient.readUTF();
                    int teacherID = Integer.parseInt(inputFromClient.readUTF());

                    admin.addCourse(courseName, teacherID);
                }
                case "5" -> {
                    StringBuilder str = new StringBuilder();
                    for (Map.Entry<Integer, String> entry : admin.getCourses().entrySet())
                        str.append("   (").append(entry.getKey()).append(")_").append(entry.getValue()).append("    ");
                    outputToClient.writeUTF(str.toString());
                }
                case "6" -> {
                    StringBuilder str = new StringBuilder();
                    for (Map.Entry<Integer, String> entry : admin.getStudents().entrySet())
                        str.append("   (").append(entry.getKey()).append(")_").append(entry.getValue()).append("    ");
                    outputToClient.writeUTF(str.toString());
                    str.setLength(0);
                    for (Map.Entry<Integer, String> entry : admin.getCourses().entrySet())
                        str.append("   (").append(entry.getKey()).append(")_").append(entry.getValue()).append("    ");
                    outputToClient.writeUTF(str.toString());
                    int studentID = Integer.parseInt(inputFromClient.readUTF());
                    int courseID = Integer.parseInt(inputFromClient.readUTF());
                    System.out.println(admin.enrollStudent(studentID, courseID));
                }
                case "7" -> {
                    break label;
                }
            }
        }
    }
}
