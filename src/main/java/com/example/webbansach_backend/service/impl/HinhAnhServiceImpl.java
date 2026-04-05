package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.Repository.SachRepository;
import com.example.webbansach_backend.dto.img.HinhAnhResponeDTO;
import com.example.webbansach_backend.mapper.HinhAnhMapper;
import com.example.webbansach_backend.service.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class HinhAnhServiceImpl implements HinhAnhService {
    @Autowired
    private RedisTemplate<String ,HinhAnhResponeDTO> redisHinhAnhBook ;
    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private HinhAnhMapper hinhAnhMapper ;
    @Override
    public List<HinhAnhResponeDTO> getAnhCuaMotSach(int maSach, int soLuong){
        // image:book:{idBook}
        List<HinhAnhResponeDTO> cached = redisHinhAnhBook.opsForList().range("image:book:"+maSach , 0 , -1) ;
        if(!cached.isEmpty()){
            if(soLuong == 1) return cached.subList(0,1) ;
            return cached;
        }
        Sach sach = sachRepository.findByMaSachAndFetchImg(maSach ).orElseThrow(()-> new RuntimeException("sách không còn hoạt dộng")) ;
        List<HinhAnhResponeDTO> hinhAnhResponeDTOS =new ArrayList<>() ;
        sach.getDanhSachHinhAnh().forEach(hinhAnh->{
            hinhAnhResponeDTOS.add(hinhAnhMapper.toDTO(hinhAnh)) ;
        });
        redisHinhAnhBook.opsForList().rightPushAll("image:book:"+maSach , hinhAnhResponeDTOS) ;
        redisHinhAnhBook.expire("image:book:"+maSach , Duration.ofHours(1));
        if(soLuong == 1) return hinhAnhResponeDTOS.subList(0,1) ;
        return hinhAnhResponeDTOS ;
    }
}
