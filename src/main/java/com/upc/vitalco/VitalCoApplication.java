package com.upc.vitalco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.upc.vitalco")
public class VitalCoApplication {

    public static void main(String[] args) {
        SpringApplication.run(VitalCoApplication.class, args);
    }

}
