package com.example.SpringSecurity;

import com.example.SpringSecurity.db.DatabaseBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringSecurityApplication {

    public static void main(String[] args) {
        String host = System.getenv().getOrDefault("DB_HOST", "localhost");
        int port = Integer.parseInt(System.getenv().getOrDefault("DB_PORT", "3307"));
        String dbName = System.getenv().getOrDefault("DB_NAME", "springsecurity");
        String user = System.getenv().getOrDefault("DB_ADMIN_USER", "root");
        String pass = System.getenv().getOrDefault("DB_ADMIN_PASS", "123123");

        try {
            DatabaseBootstrap.ensureDatabaseExists(host, port, dbName, user, pass);
        } catch (Exception e) {
            System.err.println("[DB BOOTSTRAP] Failed to ensure database: " + e.getMessage());
        }


        SpringApplication.run(SpringSecurityApplication.class, args);
    }
}
