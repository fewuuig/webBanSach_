package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.HinhThucThanhToan;
import com.example.webbansach_backend.Repository.HinhThucThanhToanRepository;
import com.example.webbansach_backend.dto.HinhThucGiaoHangResponeDTO;
import com.example.webbansach_backend.dto.HinhThucThanhToanResponeDTO;
import com.example.webbansach_backend.service.HinhThucThanhToanService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class HinhThucThanhToanServiceImpl implements HinhThucThanhToanService {
    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository ;
    @Autowired
    private ModelMapper modelMapper ;
    @Autowired
    private RedisTemplate<String , HinhThucThanhToanResponeDTO> redisHinhThucThanhToan ;
    @Override
    public List<HinhThucThanhToanResponeDTO> getHinhThucThanhToans() {

        List<HinhThucThanhToanResponeDTO> caches = redisHinhThucThanhToan.opsForList().range("payment:method" , 0 , -1) ;
        if(!caches.isEmpty()) {
            System.out.println("cache pament");
            return caches ;
        }

        List<HinhThucThanhToanResponeDTO> hinhThucThanhToanResponeDTOS= new ArrayList<>() ;
        List<HinhThucThanhToan> hinhThucThanhToans = hinhThucThanhToanRepository.findAll() ;
        for(HinhThucThanhToan hinhThucThanhToan : hinhThucThanhToans){
            HinhThucThanhToanResponeDTO hinhThucThanhToanResponeDTO  = modelMapper.map(hinhThucThanhToan,HinhThucThanhToanResponeDTO.class);
            hinhThucThanhToanResponeDTOS.add(hinhThucThanhToanResponeDTO) ;
        }
        redisHinhThucThanhToan.opsForList().rightPushAll("payment:method" , hinhThucThanhToanResponeDTOS ) ;
        return hinhThucThanhToanResponeDTOS;
    }
}
