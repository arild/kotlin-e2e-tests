package com.example.e2e.model

import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.time.Instant

class OrderRepositoryTest(val orderRepository: OrderRepository) : DatabaseTest({

    "Should store and find order" {
        val order = Order(
            id = 1,
            userId = 100,
            created = Instant.now(),
            listOf(OrderLine(price = BigDecimal(10.0)), OrderLine(price = BigDecimal(10.0)))
        )
        val saved = orderRepository.save(order)

        val result = orderRepository.findByIdOrNull(saved.id)

        result?.orderLines?.size shouldBe 2
    }
})
