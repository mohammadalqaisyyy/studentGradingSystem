package webapp;

import webapp.Admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@WebServlet("/AdminDashboard")
public class AdminControl extends HttpServlet {
    ArrayList<String> menu, out;
    Admin admin;
    String name;

    @Override
    public void init() throws ServletException {
        super.init();
        menu = new ArrayList<>();
        out = new ArrayList<>();
        menu.add("Add user");
        menu.add("View teachers");
        menu.add("View students");
        menu.add("Add course");
        menu.add("View courses");
        menu.add("Enroll student in course");
        menu.add("Exit");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        out.clear();
        name = request.getParameter("name");
        request.setAttribute("sideList", out);
        if (name == null) {
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        } else {
            try {
                admin = new Admin(name, "");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            request.setAttribute("type", "admin");
            request.setAttribute("Admin", admin);
            request.setAttribute("name", name);
            request.setAttribute("menu", menu);
            request.getRequestDispatcher("/WEB-INF/views/adminDashboard.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        out.clear();
        try {
            admin = new Admin(name, "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        request.setAttribute("type", "admin");
        request.setAttribute("admin", admin);
        request.setAttribute("name", name);
        request.setAttribute("menu", menu);
        request.setAttribute("sideList", out);
        String numberString = request.getParameter("number");
        switch (numberString) {
            case "1" -> {
                try {
                    int userType = Integer.parseInt(request.getParameter("userType"));
                    String nameUser = request.getParameter("nameUser");
                    String email = request.getParameter("email");
                    String password = request.getParameter("password");
                    admin.addUser(userType, nameUser, email, password);
                    out.add("Done");
                } catch (SQLException e) {
                    out.add("Error");
                }
            }
            case "2" -> {
                try {
                    out.add("Teachers");
                    for (Map.Entry<Integer, String> entry : admin.getTeachers().entrySet())
                        out.add(entry.getKey().toString() + ". " + entry.getValue());
                } catch (Exception e) {
                    out.add("Error");
                }
            }
            case "3" -> {
                try {
                    out.add("Students");
                    for (Map.Entry<Integer, String> entry : admin.getStudents().entrySet())
                        out.add(entry.getKey().toString() + ". " + entry.getValue());
                } catch (Exception e) {
                    out.add("Error");
                }
            }
            case "4" -> {
                try {
                    String courseName = request.getParameter("courseName");
                    int teacherID = Integer.parseInt(request.getParameter("teacherID"));
                    admin.addCourse(courseName, teacherID);
                    out.add("Done");
                } catch (Exception e) {
                    out.add("Error");
                }
            }
            case "5" -> {
                try {
                    out.add("Courses");
                    for (Map.Entry<Integer, String> entry : admin.getCourses().entrySet())
                        out.add(entry.getKey().toString() + ". " + entry.getValue());
                } catch (Exception e) {
                    out.add("Error");
                }
            }
            case "6" -> {
                try {
                    int studentID = Integer.parseInt(request.getParameter("studentID"));
                    int courseID = Integer.parseInt(request.getParameter("courseID"));
                    out.add(admin.enrollStudent(studentID, courseID));
                } catch (Exception e) {
                    out.add("Error");
                }
            }
        }
        request.setAttribute("sideList", out);
        request.getRequestDispatcher("/WEB-INF/views/adminDashboard.jsp").forward(
                request, response);
    }

}
