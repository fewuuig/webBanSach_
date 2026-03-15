package com.example.webbansach_backend.config.initializer;

import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.Entity.TheLoai;
import com.example.webbansach_backend.Repository.SachRepository;
import com.example.webbansach_backend.Repository.TheLoaiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IdBookPagination implements ApplicationRunner {
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private TheLoaiRepository theLoaiRepository ;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<TheLoai> theLoais = theLoaiRepository.findByTheLoai()  ;
        for(TheLoai tl : theLoais){
            for(Sach s : tl.getDanhSachQuyenSach()){
                String key = "page_book_id";
                redisTemplate.opsForZSet().add(key , s.getMaSach() , s.getMaSach() ) ;
                redisTemplate.opsForZSet().add("page_book_id_category:"+tl.getMaTheLoai() , s.getMaSach() , s.getMaSach());
            }
        }

    }
}
