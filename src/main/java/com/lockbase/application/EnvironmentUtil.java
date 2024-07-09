package com.lockbase.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

public class EnvironmentUtil {

    @Value("${app.author.name}")
    private String auth_name;

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        // Using setter dependency Injection
        this.env = env;
    }

    public void getAppStartInfo(){
        // Can read properties defined in the application.properties file, just use the prop key
        System.out.println("\nApplication Name: " + env.getProperty("spring.application.name"));

        System.out.println("Java Version: " + env.getProperty("java.version"));
        System.out.println("OS Version: " + env.getProperty("os.name"));

        // Getting values from custom properties file, path defined in ApplicationConfig
        System.out.println("Author: " + auth_name);
    }
}