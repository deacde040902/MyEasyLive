package com.easylive.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.easylive")
@EnableDiscoveryClient
@MapperScan("com.easylive.base.mapper")
public class EasyliveAdminRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyliveAdminRunApplication.class, args);
    }
}
