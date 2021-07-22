package com.zb.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan("com")
public class OpencvApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpencvApplication.class, args);
    }
}
