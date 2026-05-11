package com.example.demo.config;

import com.example.demo.util.JwtFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Disable CSRF because we are using JWT-based REST APIs
            .csrf(csrf -> csrf.disable())

            // Enable CORS support for frontend connection
            .cors(cors -> {})

            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                // ============================
                // PUBLIC AUTH APIs
                // ============================
                .requestMatchers(
                    "/users/login",
                    "/users/register",
                    "/admin/login",
                    "/admin/create"
                ).permitAll()

                // ============================
                // PUBLIC STATIC IMAGE APIs
                // VERY IMPORTANT for uploaded game images
                // ============================
                .requestMatchers("/images/**").permitAll()

                // ============================
                // REVIEW APIs
                // Anyone can view reviews
                // Only logged-in users can add reviews
                // ============================
                .requestMatchers("/reviews/add/**").hasRole("USER")
                .requestMatchers("/reviews/**").permitAll()

                // ============================
                // ADMIN ONLY APIs
                // ============================
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ============================
                // USER ONLY APIs
                // ============================
                .requestMatchers("/purchase/**").hasRole("USER")
                .requestMatchers("/wishlist/**").hasRole("USER")
                .requestMatchers("/user/**").hasRole("USER")

                // ============================
                // USER + ADMIN APIs
                // ============================
                .requestMatchers("/users/**").hasAnyRole("USER", "ADMIN")

                // ============================
                // PRODUCT APIs, if still used
                // ============================
                .requestMatchers("/products/public/**").permitAll()
                .requestMatchers("/products/create").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/products/update/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/products/delete/**").hasRole("ADMIN")
                .requestMatchers("/products/**").authenticated()
                .requestMatchers("/games/update/**").hasRole("USER")
                .requestMatchers("/games/delete/**").hasRole("USER")
                .requestMatchers("/games/add").hasRole("USER")
                .requestMatchers("/games/upload/**").hasRole("USER")
                .requestMatchers("/games/**").permitAll()

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // Add JWT filter before Spring Security username/password filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}