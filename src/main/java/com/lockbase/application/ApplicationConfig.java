package com.lockbase.application;

import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "com.lockbase")
@PropertySources({
        @PropertySource("classpath:custom.properties"),
})
public class ApplicationConfig {

    @Bean
    public EnvironmentUtil environmentUtil(){
        return new EnvironmentUtil();
    }
}
