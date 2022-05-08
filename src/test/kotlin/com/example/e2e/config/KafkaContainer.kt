package com.example.e2e.config

import com.example.e2e.kafka.OrderEvent
import io.kotest.assertions.fail
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.TimeUnit

private val container = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.4"))

class KafkaContainer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    private val topics = listOf("order.changed")

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        container.start()

        AdminClient
            .create(mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to container.bootstrapServers))
            .createTopics(topics.map { NewTopic(it, 1, 1) })
            .all()
            .get(5, TimeUnit.SECONDS)

        TestPropertyValues.of(
            "KAFKA_BOOTSTRAP_SERVERS=${container.bootstrapServers}"
        ).applyTo(applicationContext.environment)
    }
}

suspend fun waitUntilMessagesAreConsumed() {
    val consumerGroup = "invoicing-consumer-group"
    try {
        delay(2000)
        withTimeout(30_000L) {
            while (getLag(consumerGroup) > 0) {
                delay(200)
            }
        }
    } catch (e: TimeoutCancellationException) {
        fail("Should catch up within the timeout")
    }
}

private fun getLag(consumerGroup: String): Long = AdminClient
    .create(mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to container.bootstrapServers))
    .listConsumerGroupOffsets(consumerGroup)
    .partitionsToOffsetAndMetadata()
    .get(5, TimeUnit.SECONDS)
    .mapNotNull {
        createConsumer(consumerGroup)
            .endOffsets(listOf(it.key))[it.key]?.minus(it.value.offset())
    }
    .sum()

private fun createConsumer(consumerGroup: String) = KafkaConsumer<String, OrderEvent>(
    mapOf(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to container.bootstrapServers,
        ConsumerConfig.GROUP_ID_CONFIG to consumerGroup,
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "false",
        ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG to "10000",
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
        JsonDeserializer.TRUSTED_PACKAGES to "*"
    )
)
