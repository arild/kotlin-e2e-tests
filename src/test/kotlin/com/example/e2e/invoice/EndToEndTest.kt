package com.example.e2e.invoice

import com.example.e2e.Application
import com.example.e2e.config.KafkaContainer
import com.example.e2e.config.PostgresContainer
import com.example.e2e.config.truncateTables
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(classes = [Application::class], properties = ["spring.main.allow-bean-definition-overriding=true"])
@ContextConfiguration(initializers = [PostgresContainer::class, KafkaContainer::class])
@AutoConfigureMockMvc
class EndToEndTest(body: StringSpec.() -> Unit = {}) : StringSpec(body) {

    override fun beforeEach(testCase: TestCase) = truncateTables()
}
