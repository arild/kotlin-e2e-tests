package com.example.e2e.kafka

import com.example.e2e.model.Order
import com.example.e2e.model.OrderLine
import com.example.e2e.model.OrderRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant

data class OrderEvent(val userId: Long, val created: Instant, val orderLines: List<OrderLineEvent>)

data class OrderLineEvent(val price: BigDecimal)

@Component
class OderEventConsumer(val orderRepository: OrderRepository) {

    @KafkaListener(topics = ["order"], groupId = "invoice")
    fun consume(orderEvent: OrderEvent) {
        orderRepository.save(
            Order(
                userId = orderEvent.userId,
                created = orderEvent.created,
                orderLines = orderEvent.orderLines.map { OrderLine(price = it.price) },
            ),
        )
    }
}
