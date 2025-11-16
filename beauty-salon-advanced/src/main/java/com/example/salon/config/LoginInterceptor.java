package com.example.salon.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String path = request.getRequestURI();
        System.out.println("=== INTERCEPTOR ===");
        System.out.println("Path: " + path);
        System.out.println("Method: " + request.getMethod());
        
        // Разрешаваме достъп до login, register и публични страници без вход
        // ВАЖНО: Проверяваме пътя ПРЕДИ да проверяваме логин статуса
        if (path.startsWith("/login") || path.startsWith("/register") || path.startsWith("/css") || path.startsWith("/js")
                || path.startsWith("/img") || path.equals("/") || path.startsWith("/services")
                || path.startsWith("/pricing") || path.startsWith("/gallery") || path.startsWith("/salon")
                || path.startsWith("/shop")) {
            // Ensure session exists for shop operations
            request.getSession(true);
            System.out.println("Public path - ALLOWED");
            return true;
        }
        
        System.out.println("Protected path - checking login...");
        
        // За останалите пътища проверяваме логин
        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("USER_EMAIL") != null;

        // Ако не е логнат и се опитва да достъпи нещо друго → redirect към /login
        if (!loggedIn) {
            // Check if this is an AJAX request
            String ajaxHeader = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(ajaxHeader) || request.getHeader("Accept").contains("application/json")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"Необходимо е да влезете в профила си\"}");
                return false;
            }
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}

