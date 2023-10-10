package com.example.SGS_MVCspring.models;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class Student {
    String name;
    private final JdbcTemplate jdbcTemplate;

    public Student(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void studentInfo(String name){
        this.name = name;
    }

    public StringBuilder  showCourses() {
        String sql = """
                SELECT c.name, grade
                FROM grade g
                         join education.course c on c.id = g.course_id
                         join education.user u on g.student_id = u.id
                where u.name = ? AND
                        1 IN (SELECT enrollment_status FROM enrollment WHERE user_id = u.id AND c.id = course_id);
                """;
        List<Map<String, Object>> courses = jdbcTemplate.queryForList(sql, name);
        StringBuilder coursesStr = new StringBuilder();
        for (Map<String, Object> courseMap : courses) {
            String nameC = (String) courseMap.get("name");
            Integer gradeC = (Integer) courseMap.get("grade");
            coursesStr.append(nameC).append(" : ").append(gradeC).append("\n");
        }
        return coursesStr;
    }
}
