package com.example.webbansach_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.script.ScriptExecutor;
import org.springframework.security.core.userdetails.UserCache;

import java.util.List;

@Configuration
public class RedisLuaConfig {
    @Bean
    public DefaultRedisScript<Long> stockVoucher(){
        DefaultRedisScript<Long> script = new DefaultRedisScript<>() ;
        script.setLocation(new ClassPathResource("/lua/voucher/voucher.lua"));
        script.setResultType(Long.class) ;
        return script ;
    }
    @Bean
    public DefaultRedisScript<Long> stockOrder(){
        DefaultRedisScript<Long> script= new DefaultRedisScript<>() ;
        script.setLocation(new ClassPathResource("/lua/order/order.lua"));
        script.setResultType(Long.class);
        return script ;
    }
    @Bean
    public DefaultRedisScript<List> returnOrderTimeout(){
        DefaultRedisScript<List> script = new DefaultRedisScript<>() ;
        script.setLocation(new ClassPathResource("/lua/orderTimeout/orderTimeout.lua"));
        script.setResultType(List.class);
        return script ;
    }
    @Bean
    public DefaultRedisScript<List> paginate(){
        DefaultRedisScript<List> script = new DefaultRedisScript<>() ;
        script.setLocation(new ClassPathResource("/lua/paginate/paginate.lua"));
        script.setResultType(List.class);
        return script ;
    }
    @Bean
    public DefaultRedisScript<Long>  stats(){
        DefaultRedisScript<Long> script = new DefaultRedisScript<>() ;
        script.setLocation(new ClassPathResource("/lua/stats/statOrder.lua"));
        script.setResultType(Long.class);
        return script ;
    }
}
