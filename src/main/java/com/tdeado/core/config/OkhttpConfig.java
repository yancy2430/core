package com.tdeado.core.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkhttpConfig {

    @Bean
    public OkHttpClient OkHttpClient(){
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().callTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS).build();
        return okHttpClient;
    }
}
