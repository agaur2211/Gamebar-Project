package com.example.demo.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private jwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 🔐 STEP 1: Get Authorization Header
        String authHeader = request.getHeader("Authorization");

        String token = null;
        String email = null;
        String role = null;

        // 🔍 STEP 2: Extract Token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            try {
                email = jwtUtil.extractEmail(token);
                role = jwtUtil.extractRole(token);
            } catch (Exception e) {
                System.out.println("❌ Invalid JWT Token");
            }
        }

        // 🔐 STEP 3: Validate & Set Authentication
        if (email != null && role != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtUtil.validateToken(token, email)) {

                // ✅ Normalize role (ADMIN / USER)
                String normalizedRole = role.toUpperCase();

                // ✅ Spring requires ROLE_ prefix internally
                SimpleGrantedAuthority authority =
                        new SimpleGrantedAuthority("ROLE_" + normalizedRole);

                // ✅ Create Authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(authority)
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // ✅ Set in Security Context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("✅ AUTH SUCCESS | User: " + email + " | Role: " + normalizedRole);
            } else {
                System.out.println("❌ Token validation failed");
            }
        }

        // 🔄 Continue filter chain
        filterChain.doFilter(request, response);
    }
}