package com.artesano.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseConnectionVerifier {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionVerifier.class);

    @Bean
    ApplicationRunner verifyDatabaseConnection(JdbcTemplate jdbcTemplate) {
        return args -> {
            String database = jdbcTemplate.queryForObject(
                    "SELECT current_database()",
                    String.class
            );

            String user = jdbcTemplate.queryForObject(
                    "SELECT current_user",
                    String.class
            );

            logger.info("PostgreSQL conectado: database={}, user={}", database, user);
        };
    }
}