import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Admin extends User {
    Connection connection;
    Map<Integer, String> students, teachers, courses;

    public Admin(String name, String password) throws SQLException {
        super(name, password, "Admin");
        connection = DBConnection.getConnection();
        students = new HashMap<>();
        teachers = new HashMap<>();
        courses = new HashMap<>();
        readUsers();
        readCourses();
    }

    private void readCourses() throws SQLException {
        ResultSet resultSet = DBConnection.getQuery("SELECT * FROM course;");
        while (resultSet.next()) {
            courses.put(resultSet.getInt(1), resultSet.getString(2));
        }
    }

    private void readUsers() throws SQLException {
        ResultSet resultSet = DBConnection.getQuery("SELECT id, name, role FROM user;");
        while (resultSet.next()) {
            if (resultSet.getString(3).equals(userTypes[1]))
                teachers.put(resultSet.getInt(1), resultSet.getString(2));
            else if (resultSet.getString(3).equals(userTypes[2]))
                students.put(resultSet.getInt(1), resultSet.getString(2));
        }
    }

    public void addUser(int userType, String name, String email, String password) throws SQLException {
        if (userType < 1 || userType > 3)
            throw new IllegalAccessError("Out of available choose");

        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        String sqlQuery = """
                INSERT IGNORE INTO user (name, email, password, role)
                VALUES (?, ?, ?, ?);
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, email);
        preparedStatement.setString(3, hashedPassword);
        preparedStatement.setString(4, userTypes[userType - 1]);
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            ResultSet resultSet = DBConnection.getQuery("SELECT MAX(id) FROM user;");
            resultSet.next();
            int newID = resultSet.getInt(1);

            if (userType == 2)
                teachers.put(newID, name);
            else if (userType == 3)
                students.put(newID, name);
        }
    }

    public String enrollStudent(int studentID, int courseID) throws SQLException {
        if (!students.containsKey(studentID))
            throw new IllegalArgumentException("No student have this ID");
        if (!courses.containsKey(courseID))
            throw new IllegalArgumentException("No course have this ID");

        String sqlQueryEnroll = " { CALL enroll_student(?,?) } ";
        PreparedStatement preparedStatementGrade = connection.prepareStatement(sqlQueryEnroll);
        preparedStatementGrade.setInt(1, studentID);
        preparedStatementGrade.setInt(2, courseID);
        ResultSet resultSet = preparedStatementGrade.executeQuery();
        resultSet.next();
        return resultSet.getString("message");
    }

    public void addCourse(String courseName, int teacherID) throws SQLException {
        String sqlQuery = """
                INSERT IGNORE INTO course (name, teacher_id)
                VALUES (?, ?);
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, courseName);
        preparedStatement.setInt(2, teacherID);
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            ResultSet resultSet = DBConnection.getQuery("SELECT id, name FROM course ORDER BY id DESC LIMIT 1;");
            resultSet.next();
            courses.put(resultSet.getInt(1),resultSet.getString(2));
        }
    }

    public Map<Integer, String> getStudents() {
        return students;
    }

    public Map<Integer, String> getTeachers() {
        return teachers;
    }

    public Map<Integer, String> getCourses() {
        return courses;
    }
}
