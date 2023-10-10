package webapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@WebServlet("/TeacherDashboard")
public class TeacherControl extends HttpServlet {
    ArrayList<String> menu, out;
    Teacher teacher;
    String name;

    @Override
    public void init() throws ServletException {
        super.init();
        menu = new ArrayList<>();
        out = new ArrayList<>();
        menu.add("My courses");
        menu.add("Classes marks analysis");
        menu.add("List of student");
        menu.add("Add marks");
        menu.add("Edit mark");
        menu.add("Logout");
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
                teacher = new Teacher(name, "");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            request.setAttribute("type", "teacher");
            request.setAttribute("teacher", teacher);
            request.setAttribute("name", name);
            request.setAttribute("menu", menu);
            request.getRequestDispatcher("/WEB-INF/views/teacherDashboard.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        out.clear();
        try {
            teacher = new Teacher(name, "");
            teacher.updateMyCourses();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        request.setAttribute("type", "teacher");
        request.setAttribute("teacher", teacher);
        request.setAttribute("name", name);
        request.setAttribute("menu", menu);
        request.setAttribute("sideList", out);
        String choice = request.getParameter("number");
        switch (choice) {
            case "1" -> {
                out.add("Course ID, Course name");
                for (Map.Entry<Integer, String> entry : teacher.getMyCourses().entrySet()) {
                    String s = entry.getKey().toString() + ", " + entry.getValue();
                    out.add(s);
                }
            }
            case "2" -> {
                try {
                    String[] analysis = teacher.marksAnalysis().toString().split("/",0);
                    out.addAll(Arrays.asList(analysis));
                } catch (Exception e) {
                    out.add("Error");
                }
            }
            case "3" -> {
                try {
                    String courseID = request.getParameter("courseID");
                    out.add(teacher.classList(Integer.parseInt(courseID)).toString());
                } catch (Exception e) {
                    out.add("Error");
                }
            }
            case "4" -> {
                try {
                    String courseID = request.getParameter("courseID");
                    int size = teacher.sizeOfClass(Integer.parseInt(courseID));

                    String[] marks = request.getParameter("marks").split(" ");
                    ArrayList<Integer> intMarks = new ArrayList<>();
                    for (int i = 0; i < size; i++)
                        intMarks.add(Integer.parseInt(marks[i]));
                    teacher.addMarks(Integer.parseInt(courseID), intMarks);
                    out.add("Done");
                } catch (Exception e) {
                    out.add("Error");
                }
            }
            case "5" -> {
                try {
                    int courseID = Integer.parseInt(request.getParameter("courseID"));
                    int studentID = Integer.parseInt(request.getParameter("studentID"));
                    int newGrade = Integer.parseInt(request.getParameter("newGrade"));
                    teacher.editMark(courseID, studentID, newGrade);
                    out.add("Done");
                } catch (Exception e) {
                    out.add("Error");
                }
            }
        }
        request.setAttribute("sideList", out);
        request.getRequestDispatcher("/WEB-INF/views/teacherDashboard.jsp").forward(request, response);
    }

}
