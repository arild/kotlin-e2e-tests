package com.example.e2e.invoice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.math.BigDecimal

data class InvoiceResponse(val userId: Long, val totalSum: BigDecimal)

@ControllerAdvice
class CustomControllerAdvice {

    @ExceptionHandler(Exception::class)
    fun handleExceptions(e: Exception): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
    }
}
