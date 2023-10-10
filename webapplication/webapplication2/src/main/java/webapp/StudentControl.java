package webapp;

import webapp.Student;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudentControl {
    ArrayList<String> menu;
    Student student;

    public StudentControl(HttpServletRequest request, HttpServletResponse response, String name, String password) throws ServletException, IOException {
        if (name == null)
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        request.setAttribute("type", "student");
        request.setAttribute("name", name);
        student = new Student(name, password);
        menu = new ArrayList<>();
        menu.add("View courses");
        menu.add("Logout");
        request.setAttribute("menu", menu);
        try {
            boolean isEmpty = student.showCourses().toString().isEmpty();
            if (isEmpty)
                request.setAttribute("courses", "you don't have courses");
            else
                request.setAttribute("courses", student.showCourses());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        request.getRequestDispatcher("/WEB-INF/views/studentDashboard.jsp").forward(request, response);
    }
}
