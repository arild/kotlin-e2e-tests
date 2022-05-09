package com.example.e2e.invoice.example1

import com.example.e2e.config.EndToEndTest
import com.example.e2e.config.container.waitUntilMessagesAreConsumed
import com.example.e2e.invoice.InvoiceEventProducer
import com.example.e2e.kafka.OrderEvent
import com.example.e2e.kafka.OrderLineEvent
import org.hamcrest.CoreMatchers.equalTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


class InvoiceExportTest(
    @Autowired val webApplicationContext: WebApplicationContext,
    @Autowired val producer: InvoiceEventProducer
) : EndToEndTest({
    val mockMvc = webAppContextSetup(webApplicationContext).build()

    "Performs invoice export" {
        producer.send(
            OrderEvent(
                userId = 1,
                created = oneMonthAgo(),
                orderLines = listOf(OrderLineEvent(price = BigDecimal(10.0)), OrderLineEvent(price = BigDecimal(20.0))),
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
            .andExpect { jsonPath("$.length()", equalTo(2)) }
            .andExpect { jsonPath("$[0].userId", equalTo(1)) }
            .andExpect { jsonPath("$[0].totalSum", equalTo(30.0)) }
            .andExpect { jsonPath("$[1].userId", equalTo(2)) }
            .andExpect { jsonPath("$[1].totalSum", equalTo(10.0)) }
    }
})

fun oneMonthAgo(): Instant = LocalDateTime.now().minusMonths(1).toInstant(ZoneOffset.UTC)
