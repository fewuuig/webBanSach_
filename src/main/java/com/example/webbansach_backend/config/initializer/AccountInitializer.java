package com.example.webbansach_backend.config.initializer;

import com.example.webbansach_backend.Repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountInitializer implements ApplicationRunner {
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> emails = nguoiDungRepository.findAllEmail() ;
        List<String> usernames = nguoiDungRepository.findAllUsername() ;
        usernames.forEach(username -> redisTemplate.opsForValue().set("username:"+username ,username));
        emails.forEach(email -> redisTemplate.opsForValue().set("email:"+email , email ));
    }
    // giữ nguyên cho nó sharding
}
