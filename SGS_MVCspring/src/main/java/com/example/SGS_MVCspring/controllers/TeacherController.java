package com.example.SGS_MVCspring.controllers;

import com.example.SGS_MVCspring.models.Teacher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    Teacher teacher;
    ArrayList<String> out;
    boolean isOut;

    public TeacherController(Teacher teacher) {
        this.teacher = teacher;
        out = new ArrayList<>();
        isOut = false;
    }

    @GetMapping
    public String getDashboard(Model model, @RequestParam("name") String name) {
        if (name == null)
            return "error";

        teacher.teacherInfo(name);
        model.addAttribute("name", name);
        model.addAttribute("type", "teacher");
        model.addAttribute("sideList", out);
        return "teacherDashboard";
    }

    @PostMapping
    public String processForm(HttpServletRequest request) {
        out.clear();
        isOut = true;
        request.setAttribute("type", "teacher");
        request.setAttribute("name", teacher.getName());
        request.setAttribute("isOut", isOut);
        request.setAttribute("sideList", out);
        String choice = request.getParameter("number");
        teacher.updateMyCourses();
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
        return "teacherDashboard";
    }
}
