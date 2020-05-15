package com.tdeado.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfigurer {
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate initRestTemplate(){
        return new RestTemplateBuilder().build();
    }
}
