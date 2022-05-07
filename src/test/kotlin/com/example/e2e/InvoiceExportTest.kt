package com.example.e2e

import com.example.e2e.config.EndToEndTest
import com.example.e2e.domain.Order
import com.example.e2e.domain.OrderLine
import com.example.e2e.repository.OrderRepository
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
    @Autowired val orderRepository: OrderRepository
) : EndToEndTest({

    "Order repository test" {
        val order1 = Order(
            userId = 100L,
            orderLines = listOf(OrderLine(price = BigDecimal(10.0)), OrderLine(price = BigDecimal(10.0)))
        )
        val order2 = Order(
            userId = 200L,
            orderLines = listOf(OrderLine(price = BigDecimal(10.0)), OrderLine(price = BigDecimal(10.0)))
        )
        orderRepository.save(order1)
        orderRepository.save(order2)

        val mockMvc = webAppContextSetup(webApplicationContext).build()
        mockMvc.perform(post("/invoice/export"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()", equalTo(2)))
    }
})
