package com.example.SpringSecurity.service;

import com.example.SpringSecurity.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "***REMOVED-JWT-SECRET***");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 60_000L);
    }

    @Test
    void generateToken_shouldContainCustomUserInformation() {
        CustomUserDetails userDetails = new CustomUserDetails(
                25L,
                "alice@example.com",
                "Alice",
                "encoded-password",
                "avatar.png",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtService.generateToken(userDetails);
        String name = jwtService.extractClaim(token, claims -> claims.get("name", String.class));
        String avatar = jwtService.extractClaim(token, claims -> claims.get("avatar", String.class));
        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));

        assertThat(jwtService.extractUsername(token)).isEqualTo("alice@example.com");
        assertThat(jwtService.extractUserId(token)).isEqualTo(25L);
        assertThat(name).isEqualTo("Alice");
        assertThat(avatar).isEqualTo("avatar.png");
        assertThat(role).isEqualTo("ROLE_USER");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenBelongsToAnotherUser() {
        CustomUserDetails firstUser = new CustomUserDetails(
                25L,
                "alice@example.com",
                "Alice",
                "encoded-password",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        CustomUserDetails secondUser = new CustomUserDetails(
                26L,
                "bob@example.com",
                "Bob",
                "encoded-password",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtService.generateToken(firstUser);

        assertThat(jwtService.isTokenValid(token, secondUser)).isFalse();
    }
}
