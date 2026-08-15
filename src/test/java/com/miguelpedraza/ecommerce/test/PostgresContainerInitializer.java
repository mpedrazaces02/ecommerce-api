package com.miguelpedraza.ecommerce.test;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("ecommerce_test")
            .withUsername("postgres")
            .withPassword("postgres");

    static {
        // Start container as early as possible (class loading time)
        POSTGRES_CONTAINER.start();
        System.setProperty("spring.datasource.url", POSTGRES_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", POSTGRES_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", POSTGRES_CONTAINER.getPassword());
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        System.out.println("[Test Init] Postgres container started, jdbcUrl=" + POSTGRES_CONTAINER.getJdbcUrl());
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // No-op; properties are set via system properties in static block
    }
}
