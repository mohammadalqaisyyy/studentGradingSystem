package webapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Teacher extends User {

    Connection connection;
    Map<Integer, String> myCourses;

    public Teacher(String name, String password) throws SQLException {
        super(name, password, "Teacher");
        myCourses = new HashMap<>();
        connection = DBConnection.getConnection();
    }

    public void updateMyCourses() throws SQLException {
        myCourses.clear();
        String sqlQuery = """
                SELECT c.id AS course_id, c.name AS course_name
                FROM course c INNER JOIN user u ON c.teacher_id = u.id
                WHERE u.name = ?;
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, this.name);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            myCourses.put(resultSet.getInt(1), resultSet.getString(2));
        }
    }

    public StringBuilder marksAnalysis() throws SQLException {
        float median = 0, average = 0;
        int highest = 0, lowest = 0, count = 0, sum = 0;
        StringBuilder analysis = new StringBuilder();
        List<Integer> marks = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : myCourses.entrySet()) {
            median = 0;
            highest = 0;
            lowest = 0;
            count = 0;
            sum = 0;
            average = 0;
            String sqlQuery = """
                    SELECT g.grade
                    FROM grade g
                        JOIN enrollment e ON g.student_id = e.user_id
                    WHERE g.course_id = ? AND e.enrollment_status = 1 AND e.course_id = g.course_id;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, entry.getKey());
            ResultSet resultSet = preparedStatement.executeQuery();
            int step = 1;
            while (resultSet.next()) {
                int mark = resultSet.getInt(1);
                if (step == 1) {
                    highest = mark;
                    lowest = mark;
                    step++;
                }
                marks.add(mark);
                sum += mark;
                if (mark > highest)
                    highest = mark;
                if (mark < lowest)
                    lowest = mark;
            }
            count = marks.size();
            average = (float) sum / count;
            if (count % 2 == 0)
                median = (float) marks.get(count / 2) + marks.get(count / 2 + 1);
            else
                median = marks.get((int) count / 2 + 1);
            analysis.append(entry.getKey().toString()).append(", ").append(entry.getValue()).append("/")
                    .append("Number of Student: ").append(count).append("/")
                    .append("Average: ").append(average)
                    .append(" | Median: ").append(median).append("/")
                    .append("Highest: ").append(highest)
                    .append(" | Lowest: ").append(lowest).append("/");
        }
        return analysis;
    }

    public void editMark(int courseID, int studentID, int newGrade) throws SQLException {
        accessCourse(courseID);

        String sqlQueryGrade = " { CALL edit_grade(?,?,?) } ";
        PreparedStatement preparedStatementGrade = connection.prepareStatement(sqlQueryGrade);
        preparedStatementGrade.setInt(1, studentID);
        preparedStatementGrade.setInt(2, courseID);
        preparedStatementGrade.setInt(3, newGrade);
        preparedStatementGrade.executeQuery();
    }

    public StringBuilder classList(int courseID) throws SQLException {
        accessCourse(courseID);
        String sqlQuery = """
                SELECT g.student_id, g.grade
                FROM grade g
                    JOIN enrollment e ON g.student_id = e.user_id
                WHERE g.course_id = ? AND e.enrollment_status = 1 AND e.course_id = g.course_id;
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, courseID);
        ResultSet resultSet = preparedStatement.executeQuery();
        StringBuilder classStr = new StringBuilder();
        while (resultSet.next()) {
            classStr.append("   (");
            classStr.append(resultSet.getString(1));
            classStr.append(")");
            classStr.append("-> ");
            classStr.append(resultSet.getString(2));
            classStr.append("  ");
        }
        return classStr;
    }

    public int sizeOfClass(int courseID) throws SQLException {
        accessCourse(courseID);
        int size = 0;
        String sqlQuery = """
                SELECT e.user_id
                FROM enrollment e
                WHERE e.course_id = ? AND e.enrollment_status = 1;
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, courseID);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next())
            size++;
        return size;
    }

    public void addMarks(int courseID, List<Integer> marks) throws SQLException {
        accessCourse(courseID);

        String sqlQuery = """
                SELECT e.user_id
                FROM enrollment e
                WHERE e.course_id = ? AND e.enrollment_status = 1;
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, courseID);
        ResultSet resultSet = preparedStatement.executeQuery();

        for (Integer mark : marks) {
            if (resultSet.next()) {
                String sqlQueryGrade = " { CALL add_grade(?,?,?) } ";
                PreparedStatement preparedStatementGrade = connection.prepareStatement(sqlQueryGrade);
                preparedStatementGrade.setInt(1, resultSet.getInt(1));
                preparedStatementGrade.setInt(2, courseID);
                preparedStatementGrade.setInt(3, mark);
                preparedStatementGrade.executeUpdate();
            }
        }
    }

    public void accessCourse(int courseID) {
        if (!myCourses.containsKey(courseID))
            throw new IndexOutOfBoundsException("Out of your courses");
    }

    public Map<Integer, String> getMyCourses() {
        return myCourses;
    }
}
