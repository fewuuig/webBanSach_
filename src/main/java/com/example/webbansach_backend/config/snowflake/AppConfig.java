package com.example.webbansach_backend.config.snowflake;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator(EurekaConfig eurekaConfig) {
        return new SnowflakeIdGenerator(eurekaConfig);
    }
}
