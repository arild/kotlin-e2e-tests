package com.example.e2e.invoice

import com.example.e2e.model.Order
import com.example.e2e.model.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters

@Service
class InvoiceService(
    private val orderRepository: OrderRepository,
    private val emailNotifier: EmailNotifier,
    private val clock: Clock,
) {
    @Transactional
    fun exportInvoices(): List<InvoiceResponse> {
        val orders = orderRepository.findAll()
            .filter { previousMonthOrOlder(it) }

        val invoices = orders
            .groupBy { it.userId }
            .map {
                InvoiceResponse(
                    userId = it.key,
                    totalSum = it.value
                        .flatMap { order -> order.orderLines }
                        .fold(BigDecimal.ZERO) { acc, orderLine ->
                            acc.add(orderLine.price)
                        },
                )
            }

        orders.forEach { orderRepository.save(it.copy(exported = Instant.now(clock))) }

        emailNotifier.sendReport(invoices)

        return invoices
    }

    private fun previousMonthOrOlder(order: Order): Boolean {
        return order.created.isBefore(
            LocalDateTime.now(clock)
                .with(TemporalAdjusters.firstDayOfMonth())
                .toLocalDate()
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC),
        )
    }
}

@Service
class EmailNotifier {
    fun sendReport(invoices: List<InvoiceResponse>) {
        // For illustrating test for @Transactional
    }
}
