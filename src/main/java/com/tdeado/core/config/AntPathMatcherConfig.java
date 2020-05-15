package com.tdeado.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import java.io.File;

@Configuration
public class AntPathMatcherConfig {

    @Bean
    @ConditionalOnMissingBean
    public AntPathMatcher init(){
        AntPathMatcher pathMatcher = new AntPathMatcher();
        pathMatcher.setCachePatterns(true);
        pathMatcher.setCaseSensitive(true);
        pathMatcher.setTrimTokens(true);
        pathMatcher.setPathSeparator(File.separator);
        return pathMatcher;
    }
}
