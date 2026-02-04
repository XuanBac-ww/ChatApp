package com.example.SpringSecurity.Initializer;

import com.example.SpringSecurity.dto.request.auth.RegisterUserRequest;
import com.example.SpringSecurity.enums.Role;
import com.example.SpringSecurity.repository.IUserRepository;
import com.example.SpringSecurity.service.auth.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IAuthService authService;
    private final IUserRepository userRepository;
    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setFullName("Administrator");
            request.setEmail("admin@gmail.com");
            request.setNumberPhone("0123456789");
            request.setPassword("123");
            authService.createUser(request, Role.ROLE_ADMIN);
        }
    }
}
