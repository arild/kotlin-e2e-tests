package com.example.e2e

import com.example.e2e.config.EndToEndTest
import com.example.e2e.config.InvoiceEventProducer
import com.example.e2e.config.waitUntilMessagesAreConsumed
import com.example.e2e.kafka.OrderEvent
import com.example.e2e.kafka.OrderLineEvent
import org.hamcrest.CoreMatchers.equalTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal


class InvoiceExportTest(
    @Autowired val webApplicationContext: WebApplicationContext,
    @Autowired val producer: InvoiceEventProducer
) : EndToEndTest({

    "Order repository test" {
        producer.send(
            OrderEvent(
                userId = 10L,
                orderLines = listOf(OrderLineEvent(price = BigDecimal(10.0)), OrderLineEvent(price = BigDecimal(20.0)))
            )
        )
        producer.send(
            OrderEvent(
                userId = 20L,
                orderLines = listOf(OrderLineEvent(price = BigDecimal(10.0)))
            )
        )

        waitUntilMessagesAreConsumed()

        val mockMvc = webAppContextSetup(webApplicationContext).build()
        mockMvc.perform(post("/invoice/export"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()", equalTo(2)))
    }
})
