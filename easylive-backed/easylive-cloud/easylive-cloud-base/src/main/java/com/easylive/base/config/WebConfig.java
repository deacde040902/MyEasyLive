package com.easylive.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(value = "baseWebConfig")
public class WebConfig {

    @Bean(name = "baseRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
