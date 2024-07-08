package com.lockbase.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource("classpath:custom.properties"),
})
public class ApplicationConfig {

    @Bean
    public EnvironmentUtil environmentUtil(){
        return new EnvironmentUtil();
    }
}
