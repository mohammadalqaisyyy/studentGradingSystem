import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class StudentControl {
    Student student;
    DataOutputStream outputToClient;

    public StudentControl(String name, String password, Socket clientSocket) throws SQLException, IOException {
        student = new Student(name, password);
        outputToClient = new DataOutputStream(clientSocket.getOutputStream());
        outputToClient.writeUTF("Courses and marks: \n" + student.showCourses());
    }
}
