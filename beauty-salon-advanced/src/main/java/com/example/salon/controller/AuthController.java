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

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }
    
    @PostMapping("/register")
    public String doRegister(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            @RequestParam("confirmPassword") String confirmPassword,
                            Model model) {
        
        // Validate email
        if (!authService.isValidEmail(email)) {
            model.addAttribute("error", "Невалиден имейл адрес.");
            return "register";
        }
        
        // Validate password length
        if (password.length() < 6) {
            model.addAttribute("error", "Паролата трябва да е поне 6 символа.");
            return "register";
        }
        
        // Validate password match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Паролите не съвпадат.");
            return "register";
        }
        
        // Check if user already exists
        if (authService.userExists(email)) {
            model.addAttribute("error", "Потребител с този имейл вече съществува.");
            return "register";
        }
        
        // Register user
        boolean success = authService.register(email, password);
        
        if (success) {
            model.addAttribute("success", "Успешна регистрация! Можете да влезете в системата.");
            // Redirect to login after 2 seconds
            model.addAttribute("redirectToLogin", true);
        } else {
            model.addAttribute("error", "Грешка при регистрацията. Моля, опитайте отново.");
        }
        
        return "register";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
