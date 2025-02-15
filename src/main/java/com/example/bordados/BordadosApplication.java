package com.example.bordados;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
@EnableScheduling
public class BordadosApplication {

        public static void main(String[] args) {
                SpringApplication.run(BordadosApplication.class, args);
        }
}
