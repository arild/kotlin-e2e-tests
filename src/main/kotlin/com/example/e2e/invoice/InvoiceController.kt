package com.example.e2e.invoice

import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class InvoiceController(
    private val invoiceService: InvoiceService
) {

    @PostMapping("/invoice/export", produces = [APPLICATION_JSON_VALUE])
    fun invoiceExport(): List<InvoiceResponse> {
        return invoiceService.exportInvoices()
    }
}
