package com.example.salon.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("USER_EMAIL") != null;

        String path = request.getRequestURI();

        // Разрешаваме достъп до login и публични страници без вход
        if (path.startsWith("/login") || path.startsWith("/css") || path.startsWith("/js")
                || path.startsWith("/img") || path.equals("/") || path.startsWith("/services")
                || path.startsWith("/pricing")) {
            return true;
        }

        // Ако не е логнат и се опитва да достъпи нещо друго → redirect към /login
        if (!loggedIn) {
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}

