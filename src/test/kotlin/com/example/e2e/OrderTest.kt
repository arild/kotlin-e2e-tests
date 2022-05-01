package com.example.e2e

import com.example.e2e.domain.Order
import com.example.e2e.domain.OrderLine
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class OrderTest : StringSpec({

    "Order test" {
        val order = Order(
            id = 100L,
            listOf(OrderLine(price = BigDecimal(10.0)), OrderLine(price = BigDecimal(10.0)))
        )

        order.orderLines.size shouldBe 2
    }
})
