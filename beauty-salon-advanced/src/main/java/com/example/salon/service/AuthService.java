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
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public Optional<User> login(String email, String passwordPlain) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(passwordPlain)); // Временно; ще го криптираме по-късно
    }
}
