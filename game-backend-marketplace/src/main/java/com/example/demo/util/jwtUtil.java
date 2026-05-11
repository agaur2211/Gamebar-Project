package com.example.demo.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class jwtUtil {

    // ⚠️ IMPORTANT: fixed key (DO NOT regenerate every restart)
    private final String SECRET = "mySecretKeyMySecretKeyMySecretKey123456";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 🔥 GENERATE TOKEN
    public String generateToken(String email, String role) {

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔥 EXTRACT EMAIL
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // 🔥 EXTRACT ROLE (VERY IMPORTANT)
    public String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }

    // 🔥 VALIDATE TOKEN
    public boolean validateToken(String token, String email) {
        return (extractEmail(token).equals(email)) && !isTokenExpired(token);
    }

    // 🔥 CHECK EXPIRY
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // 🔥 GET CLAIMS
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}