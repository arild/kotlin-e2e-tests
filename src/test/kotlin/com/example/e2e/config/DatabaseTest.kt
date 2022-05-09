package com.example.e2e.config

import com.example.e2e.config.container.PostgresContainer
import com.example.e2e.config.container.truncateTables
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration


@DataJpaTest
@ContextConfiguration(initializers = [PostgresContainer::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DatabaseTest(body: StringSpec.() -> Unit = {}) : StringSpec(body) {

    override fun beforeEach(testCase: TestCase) = truncateTables()
}
