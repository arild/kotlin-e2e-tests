package com.example.e2e.repository

import com.example.e2e.config.DatabaseTest
import com.example.e2e.domain.Order
import com.example.e2e.domain.OrderLine
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

class OrderRepositoryTest(val orderRepository: OrderRepository) : DatabaseTest({

    "Order repository test" {
        val order = Order(
            id = 1L,
            userId = 100L,
            listOf(OrderLine(price = BigDecimal(10.0)), OrderLine(price = BigDecimal(10.0)))
        )
        val saved = orderRepository.save(order)

        val result = orderRepository.findByIdOrNull(saved.id)

        result?.orderLines?.size shouldBe 2
    }
})
