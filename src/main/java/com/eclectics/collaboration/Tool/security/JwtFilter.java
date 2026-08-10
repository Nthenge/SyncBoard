package com.eclectics.collaboration.Tool.security;

import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRespository userRespository;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // 1. Extract ID first to check Redis Blacklist
                String tokenId = jwtUtil.extractId(token);
                Boolean isRevoked = redisTemplate.hasKey("revoked_token:" + tokenId);

                if (Boolean.TRUE.equals(isRevoked)) {
                    sendUnauthorizedResponse(response, "Token has been logged out.");
                    return;
                }

                // 2. Extract Email and Validate Token
                String email = jwtUtil.extractEmail(token);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = userRespository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    if (jwtUtil.validateToken(token, email)) {
                        CustomUserDetails userDetails = new CustomUserDetails(user);

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities()
                                );

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (ExpiredJwtException e) {
                log.warn("JWT Expired: {}", e.getMessage());
                sendUnauthorizedResponse(response, "JWT token has expired.");
                return; // Stop filter chain execution!
            } catch (MalformedJwtException | SignatureException e) {
                log.warn("Invalid JWT: {}", e.getMessage());
                sendUnauthorizedResponse(response, "Invalid JWT token.");
                return; // Stop filter chain execution!
            } catch (Exception e) {
                log.error("Authentication failed: {}", e.getMessage());
                sendUnauthorizedResponse(response, "Authentication failed.");
                return; // Stop filter chain execution!
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 status
        response.setContentType("application/json");
        response.getWriter().write("{"
                + "\"success\": false,"
                + "\"message\": \"" + message + "\""
                + "}");
    }
}