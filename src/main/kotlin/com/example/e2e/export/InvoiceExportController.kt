package com.example.e2e.export

import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class InvoiceExportController(
    private val invoiceExportService: InvoiceExportService
) {

    @PostMapping("/invoice/export", produces = [APPLICATION_JSON_VALUE])
    fun invoiceExport(): List<InvoiceResponse> {
        return invoiceExportService.export()
    }
}
