package com.vendora.helpdesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VendoraHelpdeskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VendoraHelpdeskServiceApplication.class, args);
    }
}
