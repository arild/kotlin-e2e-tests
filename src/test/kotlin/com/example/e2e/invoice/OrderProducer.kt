package com.example.e2e.invoice

import com.example.e2e.kafka.OrderEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OrderProducer(
    private val kafkaTemplate: KafkaTemplate<String, OrderEvent>,
) {
    fun send(orderEvent: OrderEvent) {
        kafkaTemplate.send(
            "order",
            orderEvent.userId.toString(),
            orderEvent,
        )
    }
}
