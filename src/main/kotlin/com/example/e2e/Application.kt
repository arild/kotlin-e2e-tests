package com.example.e2e

import com.example.e2e.domain.Order
import com.example.e2e.domain.OrderLine
import com.example.e2e.repository.OrderRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.math.BigDecimal

@SpringBootApplication
@EnableJpaRepositories
class Application(val orderRepository: OrderRepository) : CommandLineRunner {

    override fun run(vararg args: String?) {
        orderRepository.save(
            Order(
                id = 100L,
                orderLines = listOf(OrderLine(price = BigDecimal(10.0)), OrderLine(price = BigDecimal(20.0)))
            )
        )
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
