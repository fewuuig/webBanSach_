package com.example.webbansach_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class Redisconfig {
   @Bean
    public RedisTemplate<String , Object> redisTemplate(RedisConnectionFactory factory){
       // khỏi tạo và kết nối đến redis ở Ram ( ở đây ta coi RedisTemplate<K,V> như môtkj công cụ kết nối dể giao dịch ... với RAM server Redis)
       RedisTemplate<String , Object> redisTemplate = new RedisTemplate<>() ;
       redisTemplate.setConnectionFactory(factory);

       // set key dưới dạng String : nếu không sẽ rất khó kiểm xoát key -> bug
       redisTemplate.setKeySerializer(new StringRedisSerializer());

       // set value dưới dạng json để đúng với việc trao đổi dữ liệu với client 1 cách thuận tiện nhất có thể
       // default : spring sẽ để nó là JDK -> bit -> khó lấy duẽ liệu
       // ở đây để đơn giản chúng ta sẽ sử dụng JSON ddeer cấu hình cho value
       // mọi thứ khih đưa vào redis RAM nó sẽ đc luuw dưới dạng byte thông qua bộ chuyển đổi serializer(-> byte) , DeSerializer(->bit-> json)
       redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

       redisTemplate.setHashKeySerializer(new StringRedisSerializer());
       redisTemplate.setHashValueSerializer(new StringRedisSerializer());
       redisTemplate.afterPropertiesSet();

       return redisTemplate ;
   }
}

/*
* Có cần thứ tự không? → List hoặc ZSet

Có cần loại trùng không? → Set hoặc ZSet

Có cần update từng field không? → Hash

Chỉ là cache đơn giản? → Value
* */