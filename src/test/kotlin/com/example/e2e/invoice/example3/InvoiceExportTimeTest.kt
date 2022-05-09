package com.example.e2e.invoice.example3

import com.example.e2e.config.EndToEndTest
import com.example.e2e.config.container.waitUntilMessagesAreConsumed
import com.example.e2e.invoice.InvoiceEventProducer
import com.example.e2e.kafka.OrderEvent
import com.example.e2e.kafka.OrderLineEvent
import org.hamcrest.CoreMatchers.equalTo
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters

@ContextConfiguration(classes = [ClockTestConfig::class])
class InvoiceExportTimeTest(
    val webApplicationContext: WebApplicationContext,
    val producer: InvoiceEventProducer,
) : EndToEndTest({
    val mockMvc = webAppContextSetup(webApplicationContext).build()

    "Exports orders only from previous month or older" {
        producer.send(
            OrderEvent(
                userId = 10,
                created = startOfMonth,
                orderLines = listOf(OrderLineEvent(price = BigDecimal(10.0)))
            )
        )
        producer.send(
            OrderEvent(
                userId = 11,
                created = startOfMonth.minusSeconds(1),
                orderLines = listOf(OrderLineEvent(price = BigDecimal(10.0)))
            )
        )
        waitUntilMessagesAreConsumed()

        mockMvc.post("/invoice/export")
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.length()", equalTo(1)) }
            .andExpect { jsonPath("$[0].userId", equalTo(11)) }
    }
})

private val startOfMonth: Instant = LocalDateTime.now(Clock.systemUTC())
    .with(TemporalAdjusters.firstDayOfMonth())
    .toLocalDate()
    .atStartOfDay()
    .toInstant(ZoneOffset.UTC)

@TestConfiguration
class ClockTestConfig {
    @Bean
    fun clock(): Clock {
        return Clock.fixed(startOfMonth, ZoneId.systemDefault())
    }
}
