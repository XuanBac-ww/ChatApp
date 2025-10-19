package com.example.SpringSecurity.Initializer;

import com.example.SpringSecurity.dto.request.auth.RegisterUserRequest;
import com.example.SpringSecurity.enums.Role;
import com.example.SpringSecurity.repository.IUserRepository;
import com.example.SpringSecurity.service.auth.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IAuthService authService;
    private final IUserRepository userRepository;
    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            log.info("Starting create ADMin Account");
            RegisterUserRequest request = new RegisterUserRequest();
            request.setFullName("Administrator");
            request.setEmail("admin@gmail.com");
            request.setNumberPhone("0123456789");
            request.setPassword("123");
            authService.createUser(request, Role.ROLE_ADMIN);
            log.info("Create ADMIN Account successfully {} ", request.getEmail());
        }
    }
}
