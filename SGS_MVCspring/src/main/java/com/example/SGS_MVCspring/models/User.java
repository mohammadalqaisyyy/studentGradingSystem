package com.example.SGS_MVCspring.models;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class User {
    public static final String[] userTypes = {"Admin", "Teacher", "Student", "Not User"};

    private final JdbcTemplate jdbcTemplate;

    public User(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int checkUser(String name, String password) throws SQLException {
        String sql = "SELECT * FROM user WHERE name = ?";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, name);

        for (Map<String, Object> user : users) {
            boolean userVerified = name.equals((String) user.get("name"));
            boolean passwordVerified = BCrypt.verifyer().verify(password.toCharArray(), ((String) user.get("password"))).verified;

            if (userVerified && passwordVerified) {
                String role = (String) user.get("role");
                for (int i = 0; i < userTypes.length; i++) {
                    if (userTypes[i].equals(role)) {
                        return i;
                    }
                }
            }
        }
        return 3;
    }
}
