package org.example.liquoricecartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LiquoriceCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiquoriceCartServiceApplication.class, args);
    }

}
