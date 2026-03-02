//package com.example.webbansach_backend.config.initializer;
//
//import com.example.webbansach_backend.Entity.NguoiDung;
//import com.example.webbansach_backend.Repository.NguoiDungRepository;
//import com.example.webbansach_backend.dto.ProfileUserResponeDTO;
//import com.example.webbansach_backend.mapper.ProfileUserMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//import java.util.List;
//
//@Component
//public class ProfileUserInitializer implements ApplicationRunner {
//
//    @Autowired
//    private NguoiDungRepository nguoiDungRepository ;
//    @Autowired
//    private ProfileUserMapper profileUserMapper ;
//    @Autowired
//    private RedisTemplate<String , Object> redisTemplate ;
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        List<NguoiDung> nguoiDungs = nguoiDungRepository.findAll() ;
//
//        for(NguoiDung nguoiDung : nguoiDungs){
//            String key = "profile:user:"+ nguoiDung.getTenDangNhap() ;
//            ProfileUserResponeDTO value = profileUserMapper.toDTO(nguoiDung) ;
//            redisTemplate.opsForValue().set(key , value , Duration.ofDays(30));
//        }
//    }
//}
