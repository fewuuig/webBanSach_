package com.example.webbansach_backend.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean("chatExecutor")
    public Executor executor (){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor() ;
        executor.setCorePoolSize(50); // luôn có 50 luồng trực sẵn / cx có thể cho phép nó timeout khi rảnh theo option
        executor.setMaxPoolSize(200); // từ 50 có thể scacle lên 200
        executor.setQueueCapacity(2000);
        executor.setThreadNamePrefix("chat-");
        executor.initialize();
        return executor ;

    }

}
