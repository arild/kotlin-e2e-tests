package com.example.e2e.invoice

import com.example.e2e.kafka.OrderEvent
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.stereotype.Component

@Component
class InvoiceEventProducer(
    @Value("\${KAFKA_BOOTSTRAP_SERVERS}")
    private val bootstrapServers: String
) {
    private val template: KafkaTemplate<String, OrderEvent> = KafkaTemplate(
        DefaultKafkaProducerFactory(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.RETRIES_CONFIG to 1,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            )
        )
    )

    fun send(orderEvent: OrderEvent) {
        template.send("order.changed", orderEvent)
    }
}
