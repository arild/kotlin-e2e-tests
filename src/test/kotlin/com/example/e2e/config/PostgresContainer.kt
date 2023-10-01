package com.example.e2e.config

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager

private val container = PostgreSQLContainer("postgres:15-alpine3.17")
    .withDatabaseName("invoice")
    .withUsername("invoice_admin")
    .withPassword("my_password")

class PostgresContainer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        if (!container.isRunning) {
            container.start()

            val connection = DriverManager.getConnection(
                container.jdbcUrl,
                container.username,
                container.password,
            )
            connection.prepareStatement(
                """
                DO $$
                BEGIN
                    CREATE ROLE invoice_user with password 'my_password';
                    EXCEPTION WHEN DUPLICATE_OBJECT THEN
                    RAISE NOTICE 'not creating role invoice_user -- it already exists';
                END
                $$;
                """,
            ).execute()

            connection.prepareStatement("ALTER ROLE invoice_user WITH LOGIN;").execute()
            connection.prepareStatement("ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT,INSERT,DELETE,UPDATE ON TABLES TO invoice_user;").execute()
            connection.prepareStatement("ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT,UPDATE ON SEQUENCES TO invoice_user;").execute()
        }

        TestPropertyValues.of(
            "spring.datasource.url=${container.jdbcUrl}",
            "spring.datasource.password=my_password",
            "spring.liquibase.password=my_password",
        ).applyTo(applicationContext.environment)
    }
}

fun truncateTables() {
    val connection = DriverManager.getConnection(
        container.jdbcUrl,
        container.username,
        container.password,
    )

    val allTables = "orders, order_line"
    connection.prepareStatement("TRUNCATE TABLE $allTables RESTART IDENTITY CASCADE").execute()
}
