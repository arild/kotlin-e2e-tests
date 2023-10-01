package com.example.e2e.invoice

import com.example.e2e.kafka.OrderEvent
import io.kotest.matchers.shouldBe
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.awaitility.kotlin.untilNotNull
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.stereotype.Component

@Component
class OrderProducer(
    private val kafkaTemplate: KafkaTemplate<String, OrderEvent>,
) {

    fun sendAndWaitUntilConsumed(vararg orderEvent: OrderEvent) {
        val offsetBefore = getTopicOffset()
        orderEvent.forEach { send(it) }
        await untilAsserted {
            getTopicOffset() shouldBe offsetBefore + orderEvent.size
        }
    }

    private fun send(orderEvent: OrderEvent) {
        kafkaTemplate.send(
            "order",
            orderEvent.userId.toString(),
            orderEvent,
        )
    }

    private fun getTopicOffset(): Long =
        await.untilNotNull {
            KafkaTestUtils.getCurrentOffset(EndToEndTest.kafkaContainer.bootstrapServers, "invoice", "order", 0)
        }.offset()
}
