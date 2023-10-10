package com.example.SGS_MVCspring.controllers;

import com.example.SGS_MVCspring.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;

@Controller
@RequestMapping("/login")
public class LoginController {

    final
    User user;

    public LoginController(User user) {
        this.user = user;
    }

    @GetMapping
    public String getLogin() {
        return "login";
    }

    @PostMapping
    public String processForm(Model model, @RequestParam("name") String name, @RequestParam("password") String password) {
        int userType;

        try {
            userType = user.checkUser(name, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Try to login -> " + User.userTypes[userType] + " " + name);

        if (userType == 3) {
            model.addAttribute("errorMessage", "Invalid Credentials!!");
            return "login";
        }

        switch (User.userTypes[userType]) {
            case "Admin" -> {
                return "redirect:/admin?name=" + name;
            }
            case "Teacher" -> {
                return "redirect:/teacher?name=" + name;
            }
            case "Student" -> {
                return "redirect:/student?name=" + name;
            }
        }
        return "login";
    }
}
