package com.kingdom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class KingdomApplication {

    public static void main(String[] args) {
        SpringApplication.run(KingdomApplication.class, args);
    }

}
