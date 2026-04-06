package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.DiaChiGiaoHang;
import com.example.webbansach_backend.Repository.DiaChiGiaoHangRepository;
import com.example.webbansach_backend.service.DiaChiGiaoHangCacheService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DiaChiGiaoHangCacheServiceImpl implements DiaChiGiaoHangCacheService {
    @Autowired
    private DiaChiGiaoHangRepository diaChiGiaoHangRepository ;

    //cache Ram app
    private Map<Integer, DiaChiGiaoHang> diaChiCache = new ConcurrentHashMap<>() ;
    // load luôn
    @PostConstruct
    public void cache(){
        diaChiCache = diaChiGiaoHangRepository.findAll().stream().
                collect(Collectors.toMap(DiaChiGiaoHang::getMaDiaChiGiaoHang, s->s)) ;
    }
    // get địa chỉ
    @Override
    public DiaChiGiaoHang getDiaChiCache(int maDiaChi) {
        return diaChiCache.get(maDiaChi) ;
    }

}
