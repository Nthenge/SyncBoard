package com.eclectics.collaboration.Tool.security;

import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRespository userRespository;
    private final StringRedisTemplate redisTemplate;


    public JwtFilter(JwtUtil jwtUtil, UserRespository userRespository, StringRedisTemplate redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.userRespository = userRespository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws java.io.IOException, jakarta.servlet.ServletException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            String tokenId = jwtUtil.extractId(token);
            Boolean isRevoked = redisTemplate.hasKey("revoked_token:" + tokenId);

            if (Boolean.TRUE.equals(isRevoked)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has been logged out.");
                return;
            }

            try {
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
            } catch (MalformedJwtException e) {
                System.err.println("JWT validation failed: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Authentication process failed: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
