import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Student extends User {
    public Student(String name, String password) {
        super(name, password, "Student");
    }

    public StringBuilder showCourses() throws SQLException {
        Connection connection = DBConnection.getConnection();
        String sqlQuery = """
                SELECT c.name, grade
                FROM grade g
                         join education.course c on c.id = g.course_id
                         join education.user u on g.student_id = u.id
                where u.name = ? AND
                        1 IN (SELECT enrollment_status FROM enrollment WHERE user_id = u.id AND c.id = course_id);
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, this.name);
        ResultSet resultSet = preparedStatement.executeQuery();

        StringBuilder coursesStr = new StringBuilder();
        while (resultSet.next())
            coursesStr.append(resultSet.getString(1)).append("  :  ").append(resultSet.getInt(2)).append("\n");
        return coursesStr;
    }
}
