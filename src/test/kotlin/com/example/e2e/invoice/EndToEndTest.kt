package com.example.e2e.invoice

import com.example.e2e.Application
import com.example.e2e.config.PostgresContainer
import com.example.e2e.config.truncateTables
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest(
    classes = [Application::class],
    properties = [
        "spring.main.allow-bean-definition-overriding=true",
        "spring.kafka.consumer.auto-offset-reset=earliest",
    ],
)
@ContextConfiguration(initializers = [PostgresContainer::class])
@AutoConfigureMockMvc
@Testcontainers
class EndToEndTest(body: StringSpec.() -> Unit = {}) : StringSpec(body) {

    override fun beforeEach(testCase: TestCase) = truncateTables()

    companion object {
        @Container
        val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.3")).apply { start() }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers") { kafka.bootstrapServers }
        }
    }
}
