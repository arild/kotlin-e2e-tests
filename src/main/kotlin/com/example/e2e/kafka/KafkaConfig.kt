package com.example.e2e.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
@EnableKafka
class KafkaConfig(
    @Value("\${KAFKA_BOOTSTRAP_SERVERS}")
    private val bootstrapServers: String,
) {
    @Bean("invoiceContainerFactory")
    fun invoiceContainerFactory() = ConcurrentKafkaListenerContainerFactory<String, OrderEvent>().apply {
        consumerFactory = DefaultKafkaConsumerFactory(
            mapOf(
                ConsumerConfig.GROUP_ID_CONFIG to "order-consumer-group",
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
                JsonDeserializer.TRUSTED_PACKAGES to "com.example.e2e.kafka",
            ),
        )
    }
}
