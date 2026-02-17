package com.dailywallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DailyWalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyWalletApplication.class, args);
    }
}
