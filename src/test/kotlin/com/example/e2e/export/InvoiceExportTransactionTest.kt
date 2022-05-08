package com.example.e2e.export

import com.example.e2e.config.EndToEndTest
import com.example.e2e.config.waitUntilMessagesAreConsumed
import com.example.e2e.kafka.OrderEvent
import com.example.e2e.kafka.OrderLineEvent
import com.example.e2e.model.OrderRepository
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.nulls.shouldBeNull
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal


class InvoiceExportTransactionTest(
    @Autowired val webApplicationContext: WebApplicationContext,
    @Autowired val producer: InvoiceEventProducer,
    @Autowired val orderRepository: OrderRepository,
    @MockkBean val emailNotifier: EmailNotifier
) : EndToEndTest({
    val mockMvc = webAppContextSetup(webApplicationContext).build()

    "Should rollback order updates when export fails" {
        every { emailNotifier.sendReport(any()) } throws(RuntimeException("Something went wrong"))
        producer.send(
            OrderEvent(
                userId = 1,
                created = oneMonthAgo(),
                orderLines = listOf(
                    OrderLineEvent(price = BigDecimal(10.0)),
                    OrderLineEvent(price = BigDecimal(20.0))
                )
            )
        )
        waitUntilMessagesAreConsumed()

        mockMvc.post("/invoice/export").andExpect { status().is5xxServerError }
        orderRepository.findAll().forEach { order ->
            order.exported.shouldBeNull()
        }
    }
})
