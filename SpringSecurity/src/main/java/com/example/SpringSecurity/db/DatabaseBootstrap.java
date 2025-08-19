package com.example.SpringSecurity.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseBootstrap {

    private DatabaseBootstrap() {}

    /**
     * Create database if not exists.
     *
     * @param host "127.0.0.1"
     * @param port 3307
     * @param dbName "springsecurity"
     * @param user "root"
     * @param pass "123123"
     */
    public static void ensureDatabaseExists(
            String host, int port, String dbName, String user, String pass
    ) throws SQLException {
        String url = String.format(
                "jdbc:mysql://%s:%d/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8",
                host, port
        );

        String createSql = String.format(
                "CREATE DATABASE IF NOT EXISTS `%s` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
                dbName
        );

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement st = conn.createStatement()) {
            st.executeUpdate(createSql);
            System.out.println("[DB BOOTSTRAP] Database ensured: " + dbName);
        }
    }
}
