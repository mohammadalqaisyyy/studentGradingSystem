package webapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(
                request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        int userType;
        try {
            userType = User.checkUser(name, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Try to login -> " + User.userTypes[userType] + " " + name);

        if (userType == 3) {
            request.setAttribute("errorMessage", "Invalid Credentials!!");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(
                    request, response);
        }

        switch (User.userTypes[userType]) {
            case "Admin" -> {
                String redirectURL = request.getContextPath() + "/AdminDashboard?name=" + name;
                response.sendRedirect(redirectURL);
            }
            case "Teacher" -> {
                String redirectURL = request.getContextPath() + "/TeacherDashboard?name=" + name;
                response.sendRedirect(redirectURL);
            }
            case "Student" -> new StudentControl(request, response, name, password);
        }
    }
}
