package com.example.SpringSecurity.security;

import com.example.SpringSecurity.enums.Role;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Test
    void loadUserByUsername_shouldReturnCustomUserDetails_whenUserExists() {
        User user = User.builder()
                .fullName("Alice")
                .email("alice@example.com")
                .password("secret")
                .numberPhone("0123456789")
                .role(Role.ROLE_USER)
                .build();
        user.setId(7L);

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        CustomUserDetails userDetails =
                (CustomUserDetails) customUserDetailService.loadUserByUsername("alice@example.com");

        assertThat(userDetails.getUserId()).isEqualTo(7L);
        assertThat(userDetails.getUsername()).isEqualTo("alice@example.com");
        assertThat(userDetails.getAuthorities())
                .extracting(authority -> authority.getAuthority())
                .containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailService.loadUserByUsername("missing@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }
}
