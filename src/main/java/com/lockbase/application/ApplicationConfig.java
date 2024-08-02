package com.lockbase.application;

import com.lockbase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@ComponentScan(basePackages = "com.lockbase")
@PropertySources({
        @PropertySource("classpath:custom.properties"),
})
@EnableJpaRepositories(basePackages = "com.lockbase.repository")
@EntityScan(basePackages = "com.lockbase.model")
public class ApplicationConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public EnvironmentUtil environmentUtil(){
        return new EnvironmentUtil();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found while " +
                        "authentication"));
    }
}
