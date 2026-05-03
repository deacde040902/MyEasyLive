package com.easylive.getaway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EasyliveGatewayRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyliveGatewayRunApplication.class, args);
    }
}