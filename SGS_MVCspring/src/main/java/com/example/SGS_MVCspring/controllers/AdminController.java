package com.example.SGS_MVCspring.controllers;

import com.example.SGS_MVCspring.models.Admin;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
    Admin admin;
    ArrayList<String> out;
    boolean isOut;

    public AdminController(Admin admin) {
        this.admin = admin;
        out = new ArrayList<>();
        isOut = false;
    }

    @GetMapping
    public String getDashboard(Model model, @RequestParam("name") String name) {
        if (name == null)
            return "error";

        else {
            admin.adminInfo(name);
            out.clear();
            out.add("Empty");
            model.addAttribute("name", name);
            model.addAttribute("type", "admin");
            model.addAttribute("sideList", out);
            return "adminDashboard";
        }
    }

    @PostMapping
    public String processForm(HttpServletRequest request) {
        isOut = true;
        out.clear();
        request.setAttribute("type", "admin");
        request.setAttribute("name", admin.getName());
        request.setAttribute("sideList", out);
        request.setAttribute("isOut", isOut);
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
                } catch (Exception e) {
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
        return "adminDashboard";
    }
}
