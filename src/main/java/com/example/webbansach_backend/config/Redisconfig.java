package com.example.webbansach_backend.config;

import com.example.webbansach_backend.dto.*;
import com.example.webbansach_backend.dto.Message.MessageResponeDTO;
import com.example.webbansach_backend.dto.img.HinhAnhResponeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
   @Bean
   public StringRedisTemplate redisTemplate2(RedisConnectionFactory factory){
      return new StringRedisTemplate(factory);
   }
   @Bean
   public RedisTemplate<String, MaGiamGiaUserResponeDTO> redisTemplateMaGiamGia(RedisConnectionFactory factory) {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      Jackson2JsonRedisSerializer<MaGiamGiaUserResponeDTO> serializer =
              new Jackson2JsonRedisSerializer<>(objectMapper, MaGiamGiaUserResponeDTO.class);

      RedisTemplate<String, MaGiamGiaUserResponeDTO> template = new RedisTemplate<>();
      template.setConnectionFactory(factory);
      template.setDefaultSerializer(serializer);
      template.setKeySerializer(new StringRedisSerializer());
      template.setValueSerializer(serializer);
      template.setHashKeySerializer(new StringRedisSerializer());
      template.setHashValueSerializer(serializer);
      template.afterPropertiesSet();
      return template;
   }
   @Bean
   public RedisTemplate<String , MessageResponeDTO> redisOperations(RedisConnectionFactory factory){
      ObjectMapper objectMapper = new ObjectMapper() ;
      objectMapper.registerModule(new JavaTimeModule()) ;
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      Jackson2JsonRedisSerializer<MessageResponeDTO> serializer = new Jackson2JsonRedisSerializer<>(objectMapper,MessageResponeDTO.class) ;

      RedisTemplate<String , MessageResponeDTO> template = new RedisTemplate<String, MessageResponeDTO>() ;
      template.setConnectionFactory(factory);
      template.setDefaultSerializer(serializer);
      template.setValueSerializer(serializer);
      template.setHashKeySerializer(new StringRedisSerializer());
      template.setHashValueSerializer(new StringRedisSerializer());
      template.setKeySerializer(new StringRedisSerializer());
      return template ;
   }
   @Bean
   public RedisTemplate<String , DanhGiaResponeDTO> redisDanhGia(RedisConnectionFactory factory){
      ObjectMapper objectMapper = new ObjectMapper() ;
      objectMapper.registerModule(new JavaTimeModule()) ;
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      Jackson2JsonRedisSerializer<DanhGiaResponeDTO> serializer = new Jackson2JsonRedisSerializer<>(objectMapper,DanhGiaResponeDTO.class) ;

      RedisTemplate<String , DanhGiaResponeDTO> template = new RedisTemplate<String, DanhGiaResponeDTO>() ;
      template.setConnectionFactory(factory);
      template.setDefaultSerializer(serializer);
      template.setValueSerializer(serializer);
      template.setHashKeySerializer(new StringRedisSerializer());
      template.setHashValueSerializer(new StringRedisSerializer());
      template.setKeySerializer(new StringRedisSerializer());
      return template ;
   }
   @Bean
   public RedisTemplate<String , HinhAnhResponeDTO> redisHinhAnhBook(RedisConnectionFactory factory){
      ObjectMapper objectMapper = new ObjectMapper() ;
      objectMapper.registerModule(new JavaTimeModule()) ;
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      Jackson2JsonRedisSerializer<HinhAnhResponeDTO> serializer = new Jackson2JsonRedisSerializer<>(objectMapper,HinhAnhResponeDTO.class) ;

      RedisTemplate<String , HinhAnhResponeDTO> template = new RedisTemplate<String, HinhAnhResponeDTO>() ;
      template.setConnectionFactory(factory);
      template.setDefaultSerializer(serializer);
      template.setValueSerializer(serializer);

      template.setKeySerializer(new StringRedisSerializer());
      return template ;
   }
   @Bean
   public RedisTemplate<String , DiaChiGiaoHangResponeDTO> redisDiaChi(RedisConnectionFactory factory){
      ObjectMapper objectMapper = new ObjectMapper() ;
      objectMapper.registerModule(new JavaTimeModule()) ;
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      Jackson2JsonRedisSerializer<DiaChiGiaoHangResponeDTO> serializer = new Jackson2JsonRedisSerializer<>(objectMapper,DiaChiGiaoHangResponeDTO.class) ;

      RedisTemplate<String , DiaChiGiaoHangResponeDTO> template = new RedisTemplate<String, DiaChiGiaoHangResponeDTO>() ;
      template.setConnectionFactory(factory);
      template.setDefaultSerializer(serializer);
      template.setValueSerializer(serializer);
      template.setHashKeySerializer(new StringRedisSerializer());
      template.setHashValueSerializer(new StringRedisSerializer());
      template.setKeySerializer(new StringRedisSerializer());
      return template ;
   }
   @Bean
   public RedisTemplate<String , HinhThucGiaoHangResponeDTO> redisHinhThucGiaoHang(RedisConnectionFactory factory){
      ObjectMapper objectMapper = new ObjectMapper() ;
      objectMapper.registerModule(new JavaTimeModule()) ;
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      Jackson2JsonRedisSerializer<HinhThucGiaoHangResponeDTO> serializer = new Jackson2JsonRedisSerializer<>(objectMapper,HinhThucGiaoHangResponeDTO.class) ;

      RedisTemplate<String , HinhThucGiaoHangResponeDTO> template = new RedisTemplate<String, HinhThucGiaoHangResponeDTO>() ;
      template.setConnectionFactory(factory);
      template.setDefaultSerializer(serializer);
      template.setValueSerializer(serializer);
      template.setHashKeySerializer(new StringRedisSerializer());
      template.setHashValueSerializer(new StringRedisSerializer());
      template.setKeySerializer(new StringRedisSerializer());
      return template ;
   }
   @Bean
   public RedisTemplate<String , HinhThucThanhToanResponeDTO> redisHinhThucThanhToan(RedisConnectionFactory factory){
      ObjectMapper objectMapper = new ObjectMapper() ;
      objectMapper.registerModule(new JavaTimeModule()) ;
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      Jackson2JsonRedisSerializer<HinhThucThanhToanResponeDTO> serializer = new Jackson2JsonRedisSerializer<>(objectMapper,HinhThucThanhToanResponeDTO.class) ;

      RedisTemplate<String , HinhThucThanhToanResponeDTO> template = new RedisTemplate<String, HinhThucThanhToanResponeDTO>() ;
      template.setConnectionFactory(factory);
      template.setDefaultSerializer(serializer);
      template.setValueSerializer(serializer);
      template.setHashKeySerializer(new StringRedisSerializer());
      template.setHashValueSerializer(new StringRedisSerializer());
      template.setKeySerializer(new StringRedisSerializer());
      return template ;
   }
   @Bean
   public RedisTemplate<String , Object> redisOperationsObj(RedisConnectionFactory factory){
      ObjectMapper objectMapper = new ObjectMapper() ;
      objectMapper.registerModule(new JavaTimeModule()) ;
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // cấm không cho sang dạng nano (s)
      Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper,Object.class) ;

      RedisTemplate<String , Object> template = new RedisTemplate<String, Object>() ;
      template.setConnectionFactory(factory);
      template.setDefaultSerializer(serializer);
      template.setValueSerializer(serializer);
      template.setKeySerializer(new StringRedisSerializer());
      return template ;
   }


}

/*
* Có cần thứ tự không? → List hoặc ZSet

Có cần loại trùng không? → Set hoặc ZSet

Có cần update từng field không? → Hash

Chỉ là cache đơn giản? → Value
* */