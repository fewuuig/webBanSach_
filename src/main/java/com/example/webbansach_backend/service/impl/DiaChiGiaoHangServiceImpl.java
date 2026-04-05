package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.DiaChiGiaoHang;
import com.example.webbansach_backend.Entity.DonHang;
import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Repository.DonHangRepository;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.converter.DiaChiGiaoHangResponeDTOConverter;
import com.example.webbansach_backend.dto.DiaChiGiaoHangRequestDTO;
import com.example.webbansach_backend.dto.DiaChiGiaoHangResponeDTO;
import com.example.webbansach_backend.service.DiaChiGiaoHangService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
@Transactional
@Service
public class DiaChiGiaoHangServiceImpl implements DiaChiGiaoHangService {
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private DonHangRepository donHangRepository ;
    @Autowired
    private RedisTemplate<String ,DiaChiGiaoHangResponeDTO > redisDiaChi ;

    @Override
    public List<DiaChiGiaoHangResponeDTO> getDiaChiGiaoHang(String tenDangNhap) {
        List<DiaChiGiaoHangResponeDTO> caches = redisDiaChi.opsForList().range("address:ship:user:"+tenDangNhap,0,-1) ;
        if(!caches.isEmpty()){
            System.out.println("cache dia chi");
            return caches ;
        }
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhapFetchDiaChi(tenDangNhap).orElseThrow() ;
        List<DiaChiGiaoHang> diaChiGiaoHangs = nguoiDung.getDiaChiGiaoHangs() ;
        List<DiaChiGiaoHangResponeDTO> diaChiGiaoHangResponeDTOS = new ArrayList<>() ;
        diaChiGiaoHangs.forEach(diaChi->{
            diaChiGiaoHangResponeDTOS.add(DiaChiGiaoHangResponeDTOConverter.toDiaChiGiaoHangResponeDTO(diaChi)) ;
        });
        redisDiaChi.opsForList().rightPushAll("address:ship:user:"+tenDangNhap,diaChiGiaoHangResponeDTOS) ;
        redisDiaChi.expire("address:ship:user:"+tenDangNhap , Duration.ofHours(1)) ;
        return diaChiGiaoHangResponeDTOS ;
    }

    @Override
    public void addDiaChiGiaoHang(String tenDangNhap , DiaChiGiaoHangRequestDTO diaChiGiaoHangRequestDTO) {
       NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
       DiaChiGiaoHang diaChiGiaoHang = new DiaChiGiaoHang() ;
       diaChiGiaoHang.setTinhOrCity(diaChiGiaoHangRequestDTO.getTinhOrCity());
       diaChiGiaoHang.setQuanOrHuyen(diaChiGiaoHangRequestDTO.getQuanOrHuyen());
       diaChiGiaoHang.setNguoiDung(nguoiDung);
       diaChiGiaoHang.setPhuongOrXa(diaChiGiaoHangRequestDTO.getPhuongOrXa());
       diaChiGiaoHang.setSoNha(diaChiGiaoHangRequestDTO.getSoNha());
       nguoiDung.getDiaChiGiaoHangs().add(diaChiGiaoHang) ;
        redisDiaChi.opsForList().rightPush("address:ship:user:"+tenDangNhap,DiaChiGiaoHangResponeDTOConverter.toDiaChiGiaoHangResponeDTO(diaChiGiaoHang)) ;
        redisDiaChi.expire("address:ship:user:"+tenDangNhap , Duration.ofHours(1)) ;
    }
}
