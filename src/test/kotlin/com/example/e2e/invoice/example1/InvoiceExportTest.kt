package com.example.e2e.invoice.example1

import com.example.e2e.invoice.EndToEndTest
import com.example.e2e.kafka.OrderEvent
import com.example.e2e.kafka.OrderLineEvent
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.hamcrest.CoreMatchers.equalTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class InvoiceExportTest(
    @Autowired val kafkaTemplate: KafkaTemplate<String, OrderEvent>,
    @Autowired val mockMvc: MockMvc,
) : EndToEndTest({

    "Performs invoice export" {
        kafkaTemplate.send(
            "order",
            "1",
            OrderEvent(
                userId = 1,
                created = oneMonthAgo(),
                orderLines = listOf(
                    OrderLineEvent(price = BigDecimal(10.0)),
                    OrderLineEvent(price = BigDecimal(20.0)),
                ),
            ),
        )
        kafkaTemplate.send(
            "order",
            "2",
            OrderEvent(
                userId = 2,
                created = oneMonthAgo(),
                orderLines = listOf(OrderLineEvent(price = BigDecimal(10.0))),
            ),
        )

        await untilAsserted {
            mockMvc.post("/invoice/export")
                .andExpect { status().isOk }
                .andExpect { jsonPath("$.length()", equalTo(2)) }
                .andExpect { jsonPath("$[0].userId", equalTo(1)) }
                .andExpect { jsonPath("$[0].totalSum", equalTo(30.0)) }
                .andExpect { jsonPath("$[1].userId", equalTo(2)) }
                .andExpect { jsonPath("$[1].totalSum", equalTo(10.0)) }
        }
    }
})

fun oneMonthAgo(): Instant = LocalDateTime.now().minusMonths(1).toInstant(ZoneOffset.UTC)
