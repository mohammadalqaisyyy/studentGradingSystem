import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

public class TeacherControl {
    Teacher teacher;
    Scanner scanner;
    DataInputStream inputFromClient;
    DataOutputStream outputToClient;

    public TeacherControl(String nameTeacher, String passwordTeacher, Socket clientSocket) throws SQLException, IOException {
        teacher = new Teacher(nameTeacher, passwordTeacher);
        outputToClient = new DataOutputStream(clientSocket.getOutputStream());
        inputFromClient = new DataInputStream(clientSocket.getInputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

        scanner = new Scanner(System.in);

        teacher.updateMyCourses();

        if (teacher.myCourses.isEmpty()) {
            outputToClient.writeUTF("""
                    You do not have any course, So you do not have permissions.
                    communicate with the administrator..
                    logout automatically..""");
            clientSocket.close();
        }

        String choice;
        label:
        while (true) {
            outputToClient.writeUTF(
                    """
                            \n
                            1. Update my courses.
                            2. Classes marks analysis.
                            3. list of student with grade.
                            4. Add marks
                            5. Edit mark.
                            6. Exit.
                            \n"""
            );
            choice = inputFromClient.readUTF();
            outputToClient.writeUTF(choice);
            switch (choice) {
                case "1" -> {
                    teacher.updateMyCourses();
                    outputToClient.writeUTF("Updated..");
                }
                case "2"->{
                    try {
                        StringBuilder list = new StringBuilder();
                        String[] analysis = teacher.marksAnalysis().toString().split("/",0);
                        for(String str :analysis){
                            list.append(str).append("\n");
                        }
                        outputToClient.writeUTF(list.toString());
                    } catch (Exception e) {
                        outputToClient.writeUTF(e.toString());
                    }
                }
                case "3" -> {
                    StringBuilder list = new StringBuilder();
                    for (Map.Entry<Integer, String> entry : teacher.getMyCourses().entrySet())
                        list.append("   (").append(entry.getKey()).append(")_").append(entry.getValue()).append("\n");
                    outputToClient.writeUTF(list.toString());
                    String courseID = inputFromClient.readUTF();
                    try {
                        outputToClient.writeUTF(teacher.classList(Integer.parseInt(courseID)).toString());
                    } catch (Exception e) {
                        outputToClient.writeUTF(e.toString());
                    }
                }
                case "4" -> {
                    StringBuilder list = new StringBuilder();
                    for (Map.Entry<Integer, String> entry : teacher.getMyCourses().entrySet())
                        list.append("   (").append(entry.getKey()).append(")_").append(entry.getValue()).append("\n");
                    outputToClient.writeUTF(list.toString());
                    String courseID = inputFromClient.readUTF();
                    try {
                        int sizeOfClass = teacher.sizeOfClass(Integer.parseInt(courseID));
                        outputToClient.writeUTF(Integer.toString(sizeOfClass));
                    } catch (Exception e) {
                        outputToClient.writeUTF(e.toString());
                    }
                    outputToClient.writeUTF(teacher.classList(Integer.parseInt(courseID)).toString());
                    List<Integer> marks=null;
                    try {
                        marks = (List<Integer>) objectInputStream.readObject();
                    } catch (ClassNotFoundException e) {
                        outputToClient.writeUTF(e.toString());
                    }
                    assert marks != null;
                    teacher.addMarks(Integer.parseInt(courseID), marks);
                }
                case "5" -> {
                    StringBuilder str = new StringBuilder();
                    for (Map.Entry<Integer, String> entry : teacher.getMyCourses().entrySet())
                        str.append("   (").append(entry.getKey()).append(")_").append(entry.getValue()).append("\n");
                    outputToClient.writeUTF(str.toString());
                    int courseID,studentID,newGrade;
                    try {
                        courseID = Integer.parseInt(inputFromClient.readUTF());
                        outputToClient.writeUTF(teacher.classList(courseID).toString());
                        studentID = Integer.parseInt(inputFromClient.readUTF());
                        newGrade = Integer.parseInt(inputFromClient.readUTF());
                        teacher.editMark(courseID, studentID, newGrade);
                    }
                    catch (Exception e){
                        outputToClient.writeUTF(e.toString());
                    }
                }
                case "6" -> {
                    break label;
                }
                default -> System.out.println("Not available choice!!");
            }
        }
    }
}
