package com.example.webbansach_backend.config.initializer;

import com.example.webbansach_backend.Entity.MaGiamGia;
import com.example.webbansach_backend.Entity.MaGiamGiaNguoiDung;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import com.example.webbansach_backend.Repository.MaGiamGiaNguoiDungRepository;
import com.example.webbansach_backend.Repository.MaGiamGiaRepository;
import com.example.webbansach_backend.Repository.MaGiamGiaSachRepository;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
            redisTemplate.opsForValue().set("voucher:stock:"+maGiamGia.getMaGiam() , remain );
        }

        // lâý lên danh sách lượt dùng mã mà user đã dùng

        List<MaGiamGiaNguoiDung> maGiamGiaNguoiDungs = maGiamGiaNguoiDungRepository.findAll() ;
        for(MaGiamGiaNguoiDung maGiamGiaNguoiDung : maGiamGiaNguoiDungs){
            redisTemplate.opsForValue().set("voucher:user:"+maGiamGiaNguoiDung.getMaGiamGia().getMaGiam()+":"+maGiamGiaNguoiDung.getNguoiDung().getTenDangNhap()
                                           , maGiamGiaNguoiDung.getDaDung());
        }
    }
}
