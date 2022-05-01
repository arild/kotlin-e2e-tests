package com.example.e2e.config

import io.kotest.core.spec.style.StringSpec
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager
import java.time.Duration.ofSeconds

val container: KPostgresContainer = KPostgresContainer("postgres:14.2")
    .withDatabaseName("accounting")
    .withUsername("my_user")
    .withPassword("password")
    .withStartupTimeout(ofSeconds(60))

open class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

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
        connection.prepareStatement("ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, UPDATE ON SEQUENCES TO my_user;").execute()
    }
}

class KPostgresContainer(imageName: String) : PostgreSQLContainer<KPostgresContainer>(imageName)

@DataJpaTest
@ContextConfiguration(initializers = [Initializer::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DatabaseTest(body: StringSpec.() -> Unit = {}) : StringSpec(body) {
}
