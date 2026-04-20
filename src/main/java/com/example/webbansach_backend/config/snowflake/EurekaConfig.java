package com.example.webbansach_backend.config.snowflake;

import org.springframework.context.annotation.Configuration;

@Configuration
public class EurekaConfig {
    public long getCurrentDataCenterIn5Bit() {
        // Return your datacenter ID (0 to 31)
        return 1L ;
    }

    public long getCurrentInstanceIndex() {
        // Return your machine ID (0 to 31)
        return 1L ;
    }
}
