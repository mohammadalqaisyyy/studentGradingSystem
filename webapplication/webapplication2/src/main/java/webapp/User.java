package webapp;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    String name, password, role;
    static final String[] userTypes = {"Admin", "Teacher", "Student", "Not User"};

    public User(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public static int checkUser(String name, String password) throws SQLException {
        ResultSet resultSet = DBConnection.getQuery("SELECT * FROM user;");
        while (resultSet.next()) {
            boolean userVerified = (name.equals(resultSet.getString(2)));
            boolean passwordVerified = BCrypt.verifyer().verify(password.toCharArray(), resultSet.getString(4)).verified;
            if (userVerified && passwordVerified) {
                for (int i = 0; i < userTypes.length; i++) {
                    if (userTypes[i].equals(resultSet.getString(5)))
                        return i;
                }
            }
        }
        return 3;
    }
}
