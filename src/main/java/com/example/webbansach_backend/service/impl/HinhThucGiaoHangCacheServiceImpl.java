package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.HinhThucGiaoHang;
import com.example.webbansach_backend.Repository.HinhThucGiaoHangRepository;
import com.example.webbansach_backend.service.HinhThucGiaoHangCacheService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class HinhThucGiaoHangCacheServiceImpl implements HinhThucGiaoHangCacheService {
    @Autowired
    private HinhThucGiaoHangRepository hinhThucGiaoHangRepository;

    private Map<Integer ,HinhThucGiaoHang> hinhThucGiaoHangCache = new ConcurrentHashMap<>() ;

    @PostConstruct
    private void loadHinhThucGiaoHang(){
        hinhThucGiaoHangCache = hinhThucGiaoHangRepository.findAll().
                stream().collect(Collectors.toMap(HinhThucGiaoHang::getMaHinhThucGiaoHang ,s->s)) ;
    }

    @Override
    public HinhThucGiaoHang getHinhThucGiaoHangCache(int id) {
        return hinhThucGiaoHangCache.get(id) ;
    }

}
