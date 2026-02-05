package com.eclectics.collaboration.Tool.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET;

    private final long SESSION_EXPIRATION_TIME = 1000 * 60 * 60;
    private final long RESET_CONFIRM_TIME = 1000 * 60 * 15;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString())
                .setExpiration(new Date(System.currentTimeMillis() + SESSION_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getId();
    }

    public Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public String generateResetPasswordToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "reset") // mark it as a reset token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + RESET_CONFIRM_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateEmailConfirmationToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "confirm") // mark as confirmation token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + RESET_CONFIRM_TIME)) // 15 mins
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String validateAndExtractEmailFromConfirmationToken(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (!"confirm".equals(claims.get("type", String.class))) {
            throw new RuntimeException("Invalid confirmation token");
        }

        if (claims.getExpiration().before(new Date())) {
            throw new RuntimeException("Confirmation token expired");
        }

        return claims.getSubject(); // email
    }


    public String validateAndExtractEmailFromResetToken(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (!"reset".equals(claims.get("type", String.class))) {
            throw new RuntimeException("Invalid reset token");
        }

        if (claims.getExpiration().before(new Date())) {
            throw new RuntimeException("Reset token expired");
        }

        return claims.getSubject(); // email
    }


    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String email) {
        String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
