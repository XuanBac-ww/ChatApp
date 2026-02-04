package com.example.SpringSecurity.config;

import com.example.SpringSecurity.infrastructure.SpringAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringAuditorAware();
    }
}
