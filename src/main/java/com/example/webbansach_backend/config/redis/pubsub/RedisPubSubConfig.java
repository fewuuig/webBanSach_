package com.example.webbansach_backend.config.redis.pubsub;

import com.example.webbansach_backend.config.redis.pubsub.SseRedisSubcriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisPubSubConfig {
    public static final String CHANNEL_MESSAGE = "SSE-MESSAGE" ;
    @Bean
    public RedisMessageListenerContainer container (RedisConnectionFactory factory ,
                                                    MessageListenerAdapter messageListenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer() ;
        container.setConnectionFactory(factory);
        container.addMessageListener(messageListenerAdapter , new PatternTopic(CHANNEL_MESSAGE));
        return container ;
    }
    @Bean
    public MessageListenerAdapter messageListenerAdapter(SseRedisSubcriber subcriber){
        return new MessageListenerAdapter(subcriber , "onMessage") ;
    }

}
