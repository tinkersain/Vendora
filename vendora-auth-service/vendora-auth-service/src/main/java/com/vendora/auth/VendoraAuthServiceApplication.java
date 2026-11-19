package com.vendora.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VendoraAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VendoraAuthServiceApplication.class, args);
	}

}
