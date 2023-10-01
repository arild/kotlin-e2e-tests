package com.example.e2e.invoice

import com.example.e2e.Application
import com.example.e2e.config.PostgresContainer
import com.example.e2e.config.truncateTables
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest(
    classes = [Application::class],
    properties = [
        "spring.main.allow-bean-definition-overriding=true",
    ],
)
@ContextConfiguration(initializers = [PostgresContainer::class])
@AutoConfigureMockMvc
class EndToEndTest(body: StringSpec.() -> Unit = {}) : StringSpec(body) {

    override suspend fun beforeEach(testCase: TestCase) = truncateTables()

    companion object {
        @ServiceConnection
        val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.5"))

        init { kafkaContainer.start() }
    }
}
