package com.example.SpringSecurity.config;

import com.example.SpringSecurity.security.CustomUserDetailService;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.JwtService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;
    private final CustomUserDetailService customUserDetailService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            if (token != null) {
                try {
                    String username = jwtService.extractUsername(token);

                    if (username == null) {
                        throw new BadCredentialsException("Invalid JWT: Username is null");
                    }

                    UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(token, userDetails)) {

                        Long userId = jwtService.extractUserId(token);
                        CustomUserDetails customUserDetails = ((CustomUserDetails) userDetails).withUserId(userId);

                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                customUserDetails,
                                null,
                                customUserDetails.getAuthorities()
                        );
                        accessor.setUser(auth);
                    } else {
                        throw new BadCredentialsException("Invalid JWT token");
                    }
                } catch (Exception e) {
                    throw new BadCredentialsException("JWT token invalid or expired", e);
                }
            } else {
                throw new BadCredentialsException("Missing JWT token in WebSocket CONNECT");
            }
        }
        return message;
    }
}
