package com.lockbase.application;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "com.lockbase")
@PropertySources({
        @PropertySource("classpath:custom.properties"),
})
@EnableJpaRepositories(basePackages = "com.lockbase.repository")
@EntityScan(basePackages = "com.lockbase.model")
public class ApplicationConfig {

    @Bean
    public EnvironmentUtil environmentUtil(){
        return new EnvironmentUtil();
    }
}
