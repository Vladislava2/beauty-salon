package com.example.salon.controller;

import com.example.salon.entity.User;
import com.example.salon.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

        import java.util.Optional;

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
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {

        if (!authService.isValidEmail(email)) {
            model.addAttribute("error", "Невалиден имейл адрес.");
            return "login";
        }

        Optional<User> user = authService.login(email, password);
        if (user.isPresent()) {
            session.setAttribute("USER_EMAIL", user.get().getEmail());
            return "redirect:/"; // пренасочи към начална/домашна
        } else {
            model.addAttribute("error", "Грешен имейл или парола.");
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
