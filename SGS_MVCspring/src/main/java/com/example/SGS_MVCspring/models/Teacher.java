package com.example.SGS_MVCspring.models;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class Teacher {
    String name;
    Map<Integer, String> myCourses;
    private final JdbcTemplate jdbcTemplate;

    public Teacher(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        myCourses = new HashMap<>();
        this.name = "";
    }

    public void teacherInfo(String name) {
        this.name = name;
    }

    public void updateMyCourses() {
        myCourses.clear();
        String sqlQuery = """
                SELECT c.id AS course_id, c.name AS course_name
                FROM course c INNER JOIN user u ON c.teacher_id = u.id
                WHERE u.name = ?;
                """;
        List<Map<String, Object>> courses = jdbcTemplate.queryForList(sqlQuery, this.name);
        for (Map<String, Object> course : courses) {
            int courseId = (int) course.get("course_id");
            String courseName = (String) course.get("course_name");
            myCourses.put(courseId, courseName);
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
            List<Map<String, Object>> courses = jdbcTemplate.queryForList(sqlQuery, entry.getKey());
            int step = 1;
            for (Map<String, Object> course : courses) {
                int mark = (int) course.get("grade");
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

    public StringBuilder classList(int courseID) {
        accessCourse(courseID);
        String sqlQuery = """
                SELECT g.student_id, g.grade
                FROM grade g
                    JOIN enrollment e ON g.student_id = e.user_id
                WHERE g.course_id = ? AND e.enrollment_status = 1 AND e.course_id = g.course_id;
                """;
        List<Map<String, Object>> classList = jdbcTemplate.queryForList(sqlQuery, courseID);
        StringBuilder classStr = new StringBuilder();
        for (Map<String, Object> c : classList){
            int student_id =(int)c.get("student_id");
            int grade = (int) c.get("grade");
            classStr.append("   (");
            classStr.append(student_id);
            classStr.append(")");
            classStr.append("-> ");
            classStr.append(grade);
            classStr.append("  ");

        }
        return classStr;
    }

    public int sizeOfClass(int courseID) {
        accessCourse(courseID);
        String sqlQuery = """
                SELECT e.user_id
                FROM enrollment e
                WHERE e.course_id = ? AND e.enrollment_status = 1;
                """;
        List<Map<String, Object>> classList = jdbcTemplate.queryForList(sqlQuery, courseID);
        return classList.size();
    }

    public void editMark(int courseID, int studentID, int newGrade) {
        accessCourse(courseID);
        String sqlQueryGrade = "CALL edit_grade(?,?,?)";
        jdbcTemplate.update(sqlQueryGrade, preparedStatement -> {
            preparedStatement.setInt(1, studentID);
            preparedStatement.setInt(2, courseID);
            preparedStatement.setInt(3, newGrade);
        });
    }

    public void addMarks(int courseID, List<Integer> marks) {
        accessCourse(courseID);
        String sqlQuery = """
                SELECT e.user_id
                FROM enrollment e
                WHERE e.course_id = ? AND e.enrollment_status = 1;
                """;
        List<Integer> userIds = jdbcTemplate.query(sqlQuery, new Object[]{courseID}, (rs, rowNum) -> rs.getInt("user_id"));
        String sqlQueryGrade = "CALL add_grade(?,?,?)";
        int i = 0;
        for (Integer userId : userIds) {
            int mark = marks.get(i);
            jdbcTemplate.update(sqlQueryGrade, preparedStatement -> {
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, courseID);
                preparedStatement.setInt(3, mark);
            });
            i++;
        }
    }

    public void accessCourse(int courseID) {
        if (!myCourses.containsKey(courseID))
            throw new IndexOutOfBoundsException("Out of your courses");
    }


    public Map<Integer, String> getMyCourses() {
        return myCourses;
    }

    public String getName() {
        return name;
    }
}