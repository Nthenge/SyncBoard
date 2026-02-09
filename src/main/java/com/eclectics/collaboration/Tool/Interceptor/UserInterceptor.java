package com.eclectics.collaboration.Tool.Interceptor;

import com.eclectics.collaboration.Tool.security.JwtUtil;
import com.eclectics.collaboration.Tool.security.UserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
@Slf4j
public class UserInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    String email = jwtUtil.extractEmail(token);

                    if (email != null && accessor.getUser() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );

                            accessor.setUser(authentication);

                            log.info("WebSocket connection authenticated for user: {}", email);
                        } else {
                            log.warn("WebSocket connection failed: Invalid JWT token for user {}", email);
                        }
                    }
                } catch (Exception e) {
                    log.error("WebSocket Authentication Error: {}", e.getMessage());
                }
            }
        }

        return message;
    }
}
