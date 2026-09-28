package com.lsfashioncloset;

import com.lsfashioncloset.config.MercadoPagoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MercadoPagoProperties.class)
public class FashionClosetApplication {
    public static void main(String[] args) {
        SpringApplication.run(FashionClosetApplication.class, args);
    }
}
