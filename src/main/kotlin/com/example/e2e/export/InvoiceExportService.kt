package com.example.e2e.export

import com.example.e2e.model.OrderRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import javax.transaction.Transactional

@Service
class InvoiceExportService(
    private val orderRepository: OrderRepository,
    private val emailNotifier: EmailNotifier
) {
    @Transactional
    fun export(): List<InvoiceResponse> {
        val orders = orderRepository.findAll()

        val invoices = orders
            .groupBy { it.userId }
            .map {
                InvoiceResponse(
                    userId = it.key,
                    totalSum = it.value
                        .flatMap { order -> order.orderLines }
                        .fold(BigDecimal.ZERO) { acc, orderLine -> acc.add(orderLine.price) }
                )
            }

        orders.forEach { orderRepository.save(it.copy(exported = Instant.now())) }

        emailNotifier.sendReport(invoices)

        return invoices
    }
}

@Service
class EmailNotifier {
    fun sendReport(invoices: List<InvoiceResponse>) {
        // For illustrating test for @Transactional
    }
}
