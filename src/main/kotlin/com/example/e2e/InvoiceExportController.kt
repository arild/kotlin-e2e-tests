package com.example.e2e

import com.example.e2e.repository.OrderRepository
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class InvoiceExportController(
    private val orderRepository: OrderRepository,
) {

    @PostMapping("/invoice/export", produces = [APPLICATION_JSON_VALUE])
    fun invoiceExport(): List<InvoiceResponse> {
        return orderRepository.findAll()
            .groupBy { it.userId }
            .map { InvoiceResponse(userId = it.key) }
    }
}

data class InvoiceResponse(val userId: Long)
