package com.example.e2e.kafka

import com.example.e2e.domain.Order
import com.example.e2e.domain.OrderLine
import com.example.e2e.repository.OrderRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.io.Serializable
import java.math.BigDecimal

data class OrderEvent(val userId: Long, val orderLines: List<OrderLineEvent>) : Serializable

data class OrderLineEvent(val price: BigDecimal) : Serializable

@Component
class InvoiceEventConsumer(val orderRepository: OrderRepository) {

    @KafkaListener(topics = ["invoicing.changed"], containerFactory = "invoiceContainerFactory")
    fun consume(orderEvent: OrderEvent) {
        orderRepository.save(
            Order(
                userId = orderEvent.userId,
                orderLines = orderEvent.orderLines.map { OrderLine(price = it.price) }
            )
        )
    }
}
