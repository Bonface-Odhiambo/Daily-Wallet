package com.dailywallet.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class DotenvConfig {
    
    @PostConstruct
    public void configure() {
        try {
            // Try to load .env from current directory and parent directories
            Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
            
            System.out.println("Loading .env file...");
            
            // Set system properties for Spring Boot to use
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                
                System.out.println("Setting environment variable: " + key);
                System.setProperty(key, value);
            });
            
            System.out.println("Successfully loaded .env file");
            
        } catch (Exception e) {
            System.out.println("Could not load .env file: " + e.getMessage());
            System.out.println("Using default configuration or system environment variables");
        }
    }
}
