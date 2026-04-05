package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.HinhThucGiaoHang;
import com.example.webbansach_backend.Entity.HinhThucThanhToan;
import com.example.webbansach_backend.Repository.HinhThucGiaoHangRepository;
import com.example.webbansach_backend.Repository.HinhThucThanhToanRepository;
import com.example.webbansach_backend.dto.HinhThucGiaoHangResponeDTO;
import com.example.webbansach_backend.dto.HinhThucThanhToanResponeDTO;
import com.example.webbansach_backend.service.HinhThucGiaoHangService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
public class HinhThucGiaoHangServiceImpl implements HinhThucGiaoHangService {
    @Autowired
    private HinhThucGiaoHangRepository hinhThucGiaoHangRepository ;
    @Autowired
    private ModelMapper modelMapper ;
    @Autowired
    private RedisTemplate<String , HinhThucGiaoHangResponeDTO> redisHinhThucGiaoHang ;
    @Override
    public List<HinhThucGiaoHangResponeDTO> getHinhThucGiaoHang() {
        List<HinhThucGiaoHangResponeDTO> caches = redisHinhThucGiaoHang.opsForList().range("ship:method" , 0 , -1) ;
        if(!caches.isEmpty()) {
            System.out.println("cache ship");
            return caches ;
        }
        List<HinhThucGiaoHangResponeDTO> hinhThucThanhToanResponeDTOS = new ArrayList<>() ;
        List<HinhThucGiaoHang> hinhThucGiaoHangs = hinhThucGiaoHangRepository.findAll() ;
        for(HinhThucGiaoHang hinhThucGiaoHang : hinhThucGiaoHangs){
            HinhThucGiaoHangResponeDTO hinhThucGiaoHangResponeDTO = modelMapper.map(hinhThucGiaoHang , HinhThucGiaoHangResponeDTO.class) ;
            hinhThucThanhToanResponeDTOS.add(hinhThucGiaoHangResponeDTO) ;
        }
        redisHinhThucGiaoHang.opsForList().rightPushAll("ship:method" , hinhThucThanhToanResponeDTOS) ;
        return hinhThucThanhToanResponeDTOS ;
    }
}
