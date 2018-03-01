package com.kingdom

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableTransactionManagement
@SpringBootApplication
object KingdomApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        SpringApplication.run(KingdomApplication::class.java, *args)
    }

}
