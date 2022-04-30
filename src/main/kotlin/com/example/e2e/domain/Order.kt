package com.example.e2e.domain

data class Order(
    val userId: Long,
    val orderLines: List<OrderLine>
)
