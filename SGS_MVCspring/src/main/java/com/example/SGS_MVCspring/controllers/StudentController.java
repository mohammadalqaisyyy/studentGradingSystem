package com.example.SGS_MVCspring.controllers;

import com.example.SGS_MVCspring.models.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/student")
public class StudentController {

    final
    Student student;

    public StudentController(Student student) {
        this.student = student;
    }

    @GetMapping
    public String getCheck(Model model, @RequestParam("name") String name) {
        if (name == null)
            return "error";

        model.addAttribute("name", name);
        model.addAttribute("type", "student");

        student.studentInfo(name);

        try {
            boolean isEmpty = student.showCourses().toString().isEmpty();
            if (isEmpty)
                model.addAttribute("courses", "you don't have courses");
            else
                model.addAttribute("courses", student.showCourses().toString().replace("\n", ", "));
        } catch (Exception e) {
            model.addAttribute("courses", "Error");
        }

        return "studentDashboard";
    }
}