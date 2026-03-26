package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.dto.ProfileUserResponeDTO;
import com.example.webbansach_backend.exception.NotFoundException;
import com.example.webbansach_backend.mapper.ProfileUserMapper;
import com.example.webbansach_backend.service.ProfileUserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Transactional
public class ProfileUserServiceImpl implements ProfileUserService {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate ;
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private ProfileUserMapper profileUserMapper ;
    @Autowired
    private ModelMapper modelMapper ;
    @Override
    public ProfileUserResponeDTO getProfileUser(String tenDangNhap) {
        String key = "profile:user:"+tenDangNhap ;
        ProfileUserResponeDTO cached = (ProfileUserResponeDTO) redisTemplate.opsForValue().get(key) ;
        if(cached != null){
            return cached ;
        }
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow(()->new NotFoundException("not find:"+tenDangNhap)) ;

        ProfileUserResponeDTO profileUserResponeDTO = modelMapper.map(nguoiDung , ProfileUserResponeDTO.class) ;

        redisTemplate.opsForValue().set(key,profileUserResponeDTO , Duration.ofHours(12));
        return profileUserResponeDTO ;
    }
}
