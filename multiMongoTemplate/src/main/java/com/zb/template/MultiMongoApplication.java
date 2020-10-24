package com.zb.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MultiMongoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MultiMongoApplication.class, args);
    }
}
