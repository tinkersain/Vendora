
package com.vendora.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class VendoraEurekaServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(
            VendoraEurekaServerApplication.class,
            args
        );
    }
}
