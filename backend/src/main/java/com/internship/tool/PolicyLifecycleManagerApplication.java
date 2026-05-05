package com.internship.tool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PolicyLifecycleManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolicyLifecycleManagerApplication.class, args);
    }
}