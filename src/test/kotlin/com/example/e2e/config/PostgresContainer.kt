package com.example.e2e.config

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager
import java.time.Duration

private val container = PostgreSQLContainer("postgres:14.2")
    .withDatabaseName("accounting")
    .withUsername("my_user")
    .withPassword("password")
    .withStartupTimeout(Duration.ofSeconds(30))

class PostgresContainer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        container.start()

        val connection = DriverManager.getConnection(
            container.jdbcUrl,
            container.username,
            container.password
        )
        connection.prepareStatement(
            """
            DO $$
            BEGIN
                CREATE ROLE my_user with password 'password';
                EXCEPTION WHEN DUPLICATE_OBJECT THEN
                RAISE NOTICE 'not creating role my_user -- it already exists';
            END
            $$;
            """
        ).execute()

        connection.prepareStatement("ALTER ROLE my_user WITH LOGIN;").execute()
        connection.prepareStatement("ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT,INSERT,DELETE,UPDATE ON TABLES TO my_user;").execute()
        connection.prepareStatement("ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT,UPDATE ON SEQUENCES TO my_user;").execute()

        TestPropertyValues.of(
            "spring.datasource.url=${container.jdbcUrl}",
            "spring.datasource.password=password",
        ).applyTo(applicationContext.environment)
    }
}

fun truncateTables() {
    val connection = DriverManager.getConnection(
        container.jdbcUrl,
        container.username,
        container.password
    )

    val allTables = "orders, order_line"
    connection.prepareStatement("TRUNCATE TABLE $allTables RESTART IDENTITY CASCADE").execute()
}
