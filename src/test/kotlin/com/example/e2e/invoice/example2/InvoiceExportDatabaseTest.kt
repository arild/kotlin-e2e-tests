package com.example.e2e.invoice.example2

import com.example.e2e.config.EndToEndTest
import com.example.e2e.config.container.waitUntilMessagesAreConsumed
import com.example.e2e.invoice.InvoiceEventProducer
import com.example.e2e.invoice.example1.oneMonthAgo
import com.example.e2e.kafka.OrderEvent
import com.example.e2e.kafka.OrderLineEvent
import com.example.e2e.model.OrderRepository
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal


class InvoiceExportDatabaseTest(
    val webApplicationContext: WebApplicationContext,
    val producer: InvoiceEventProducer,
    val orderRepository: OrderRepository
) : EndToEndTest({
    val mockMvc = webAppContextSetup(webApplicationContext).build()

    "Marks orders as exported during export" {
        producer.send(
            OrderEvent(
                userId = 1,
                created = oneMonthAgo(),
                orderLines = listOf(OrderLineEvent(price = BigDecimal(10.0)), OrderLineEvent(price = BigDecimal(20.0)))
            )
        )
        producer.send(
            OrderEvent(
                userId = 2,
                created = oneMonthAgo(),
                orderLines = listOf(OrderLineEvent(price = BigDecimal(10.0)))
            )
        )
        waitUntilMessagesAreConsumed()

        mockMvc.post("/invoice/export")
            .andExpect { status().isOk }

        orderRepository.findAll().forEach { order ->
            order.exported.shouldNotBeNull()
        }
    }
})
