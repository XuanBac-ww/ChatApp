package com.example.SpringSecurity.service.user;

import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserValidationService userValidationService;

    @Test
    void findByEmail_shouldReturnTrue_whenUserExists() {
        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(new User()));

        boolean exists = userValidationService.findByEmail("alice@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void validateAndGetUserByEmail_shouldThrowAppException_whenUserDoesNotExist() {
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userValidationService.validateAndGetUserByEmail("missing@example.com"))
                .isInstanceOf(AppException.class)
                .hasMessage("User Not Found");
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        User user = new User();
        user.setId(10L);
        user.setEmail("alice@example.com");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        User result = userValidationService.findById(10L);

        assertThat(result).isSameAs(user);
    }
}
