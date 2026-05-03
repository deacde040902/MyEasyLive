package com.easylive.interact;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.easylive",exclude = {
        ElasticsearchRestClientAutoConfiguration.class})
@EnableDiscoveryClient
@EnableAsync
@MapperScan("com.easylive.base.mapper")
public class EasyliveInteractApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyliveInteractApplication.class, args);
    }
}