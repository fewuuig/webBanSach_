package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.dto.ProfileUserResponeDTO;
import com.example.webbansach_backend.dto.profileUser.UpdateProfileUserDTO;
import com.example.webbansach_backend.exception.NotFoundException;
import com.example.webbansach_backend.mapper.NguoiDungMapper;
import com.example.webbansach_backend.mapper.ProfileUserMapper;
import com.example.webbansach_backend.service.ProfileUserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    @Autowired
    private NguoiDungMapper nguoiDungMapper ;
    @Override
    public ProfileUserResponeDTO getProfileUser(String tenDangNhap) {
        String key = "profile:user:"+tenDangNhap ;
        ProfileUserResponeDTO cached = (ProfileUserResponeDTO) redisTemplate.opsForValue().get(key) ;
        if(cached != null){
            return cached ;
        }
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow(()->new NotFoundException("not find:"+tenDangNhap)) ;

        ProfileUserResponeDTO profileUserResponeDTO = modelMapper.map(nguoiDung , ProfileUserResponeDTO.class) ;

        redisTemplate.opsForValue().set(key,profileUserResponeDTO , Duration.ofHours(1));
        return profileUserResponeDTO ;
    }

    @Override
    @Transactional
    public void updateProfileUser( String tenDangNhap,UpdateProfileUserDTO updateProfileUserDTO){
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow(()->new RuntimeException("người dùng không hợp lệ")) ;

        nguoiDungMapper.updateNguoiDung(updateProfileUserDTO , nguoiDung);
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        redisTemplate.delete("profile:user:"+tenDangNhap) ;
                    }
                }
        );
    }
}
