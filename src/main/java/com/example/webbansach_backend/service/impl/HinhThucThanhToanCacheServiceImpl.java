package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.HinhThucThanhToan;
import com.example.webbansach_backend.Repository.HinhThucThanhToanRepository;
import com.example.webbansach_backend.service.HinhThucThanhToanCacheService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class HinhThucThanhToanCacheServiceImpl implements HinhThucThanhToanCacheService {

    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository ;

    private Map<Integer ,HinhThucThanhToan> hinhThucThanhToanCache = new ConcurrentHashMap<>() ;
    @PostConstruct
    private void loadHinhThucTT(){
        hinhThucThanhToanCache =hinhThucThanhToanRepository.findAll().
                stream().collect(Collectors.toMap(HinhThucThanhToan::getMaHinhThucThanhToan,s->s)) ;
    }
    @Override
    public HinhThucThanhToan getHinhThucThanhToanCache(int maHinhThucThanhToan) {
        return hinhThucThanhToanCache.get(maHinhThucThanhToan) ;
    }
}
