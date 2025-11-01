package com.example.salon.service;

import com.example.salon.entity.User;
import com.example.salon.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Допълнителна проверка, освен @Email от модела (за бързо feedback в контролера)
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // More strict email validation - requires proper domain with TLD
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public Optional<User> login(String email, String passwordPlain) {
        System.out.println("Attempting login with email: " + email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            System.out.println("User found: " + user.get().getEmail());
            System.out.println("Password check: " + user.get().getPassword().equals(passwordPlain));
        } else {
            System.out.println("User not found in database");
            // Debug: print all users in database
            userRepository.findAll().forEach(u -> 
                System.out.println("Existing user: " + u.getEmail() + " / " + u.getPassword()));
        }
        return user.filter(u -> u.getPassword().equals(passwordPlain)); // Временно; ще го криптираме по-късно
    }
    
    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    public boolean register(String email, String passwordPlain) {
        try {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(passwordPlain); // Временно без криптиране
            userRepository.save(newUser);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
