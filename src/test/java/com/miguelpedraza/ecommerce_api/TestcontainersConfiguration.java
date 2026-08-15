package com.miguelpedraza.ecommerce_api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/* test configuration removed — lifecycle managed by BaseFunctionalTest */
/* conditional removed */
public class TestcontainersConfiguration {

	private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
			.withDatabaseName("ecommerce_test")
			.withUsername("postgres")
			.withPassword("postgres");

	// static startup moved to BaseFunctionalTest (Testcontainers-managed lifecycle)

	@Bean
	public PostgreSQLContainer<?> postgresContainer() {
		return POSTGRES_CONTAINER;
	}
}
