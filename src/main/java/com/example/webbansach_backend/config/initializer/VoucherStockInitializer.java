package com.example.webbansach_backend.config.initializer;

import com.example.webbansach_backend.Entity.MaGiamGia;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import com.example.webbansach_backend.Repository.MaGiamGiaNguoiDungRepository;
import com.example.webbansach_backend.Repository.MaGiamGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VoucherStockInitializer implements ApplicationRunner {
    @Autowired
    private MaGiamGiaRepository maGiamGiaRepository;
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    private MaGiamGiaNguoiDungRepository maGiamGiaNguoiDungRepository ;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<MaGiamGia> maGiamGias = maGiamGiaRepository.findByTrangThaiMaGiamGia(TrangThaiMaGiamGia.DANG_HOAT_DONG) ;
        for(MaGiamGia maGiamGia : maGiamGias){
            Integer remain = maGiamGia.getSoLuong() - maGiamGia.getSoMaDaDung() ;
            redisTemplate.opsForValue().set("stock:voucher:"+maGiamGia.getMaGiam() , remain );
        }

    }
}
