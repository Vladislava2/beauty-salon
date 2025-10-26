package com.example.salon.controller;

import com.example.salon.entity.User;
import com.example.salon.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login"; // templates/login.html
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam("email") String email,
                          @RequestParam("password") String password,
                          HttpServletRequest request,
                          Model model) {

        if (!authService.isValidEmail(email)) {
            model.addAttribute("error", "Невалиден имейл адрес.");
            return "login";
        }

        return authService.login(email, password)
                .map(u -> {
                    // store user email in session
                    request.getSession(true).setAttribute("USER_EMAIL", u.getEmail());
                    return "redirect:/";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Грешен имейл или парола.");
                    return "login";
                });
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
