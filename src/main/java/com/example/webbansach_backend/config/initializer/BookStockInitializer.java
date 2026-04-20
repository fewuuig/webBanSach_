package com.example.webbansach_backend.config.initializer;

import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.Repository.SachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookStockInitializer implements ApplicationRunner {
    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Sach> saches = sachRepository.findAll() ;
        if(saches == null ) return ;

        // đưa số sách còn trong kho lên redis lên redis
        for(Sach sach : saches){
            String key = "stock:book:{ws}:"+sach.getMaSach()  ;  // book:{maSach} quantity
            redisTemplate.opsForValue().set(key , sach.getSoLuong());
        }
    }
}
