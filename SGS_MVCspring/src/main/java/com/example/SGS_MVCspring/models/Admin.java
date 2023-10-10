package com.example.SGS_MVCspring.models;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.SGS_MVCspring.models.User.userTypes;

@Repository
public class Admin {
    String name;
    Map<Integer, String> students, teachers, courses;
    private final JdbcTemplate jdbcTemplate;

    public Admin(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.name = "";
        students = new HashMap<>();
        teachers = new HashMap<>();
        courses = new HashMap<>();
        readUsers();
        readCourses();
    }

    public void adminInfo(String name) {
        this.name = name;
    }

    private void readCourses() {
        String sqlQuery = "SELECT * FROM course";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery);
        for (Map<String, Object> row : rows) {
            int courseId = (int) row.get("id");
            String courseName = (String) row.get("name");
            courses.put(courseId, courseName);
        }
    }

    private void readUsers() {
        String sqlQuery = "SELECT id, name, role FROM user";
        List<Map<String, Object>> userList = jdbcTemplate.queryForList(sqlQuery);
        for (Map<String, Object> user : userList) {
            int id = (int) user.get("id");
            String name = (String) user.get("name");
            String role = (String) user.get("role");

            if (role.equals(userTypes[1])) {
                teachers.put(id, name);
            } else if (role.equals(userTypes[2])) {
                students.put(id, name);
            }
        }
    }

    public void addUser(int userType, String name, String email, String password) {
        if (userType < 1 || userType > 3)
            throw new IllegalArgumentException("Out of available choose");

        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        String sqlQuery = "INSERT IGNORE INTO user (name, email, password, role) VALUES (?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(sqlQuery, name, email, hashedPassword, userTypes[userType - 1]);

        if (rowsAffected > 0) {
            Integer newID = jdbcTemplate.queryForObject("SELECT MAX(id) FROM user", Integer.class);
            if (newID != null) {
                if (userType == 2)
                    teachers.put(newID, name);
                else if (userType == 3)
                    students.put(newID, name);
            }
        }
    }

    public String enrollStudent(int studentID, int courseID) {
        if (!students.containsKey(studentID))
            throw new IllegalArgumentException("No student has this ID");
        if (!courses.containsKey(courseID))
            throw new IllegalArgumentException("No course has this ID");

        return jdbcTemplate.execute(
                "{ CALL enroll_student(?, ?) }",
                (CallableStatementCallback<String>) cs -> {
                    cs.setInt(1, studentID);
                    cs.setInt(2, courseID);
                    cs.execute();
                    ResultSet resultSet = cs.getResultSet();
                    resultSet.next();
                    return resultSet.getString("message");
                }
        );
    }

    public void addCourse(String courseName, int teacherID) {
        String sqlQuery = "INSERT IGNORE INTO course (name, teacher_id) VALUES (?, ?)";
        int rowsAffected = jdbcTemplate.update(sqlQuery, courseName, teacherID);

        if (rowsAffected > 0) {
            int courseId = jdbcTemplate.queryForObject("SELECT id FROM course ORDER BY id DESC LIMIT 1", Integer.class);
            courses.put(courseId, courseName);
        }
    }

    public String getName() {
        return name;
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
