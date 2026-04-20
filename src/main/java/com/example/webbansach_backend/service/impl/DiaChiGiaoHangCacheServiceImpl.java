package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.DiaChiGiaoHang;
import com.example.webbansach_backend.Repository.DiaChiGiaoHangRepository;
import com.example.webbansach_backend.service.DiaChiGiaoHangCacheService;
import com.example.webbansach_backend.service.DiaChiGiaoHangService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiaChiGiaoHangCacheServiceImpl implements DiaChiGiaoHangCacheService {
    @Autowired
    private DiaChiGiaoHangRepository diaChiGiaoHangRepository ;
    private Map<Integer , DiaChiGiaoHang> diaChiGiaoHangHashMap = new HashMap<>();
    @PostConstruct
    public void loadId(){
        List<DiaChiGiaoHang> diaChiGiaoHangs = diaChiGiaoHangRepository.findAll() ;
        diaChiGiaoHangHashMap = diaChiGiaoHangs.stream().collect(Collectors.toMap(DiaChiGiaoHang::getMaDiaChiGiaoHang, s->s)) ;
    }
    @Override
    public DiaChiGiaoHang getDiaChi(int maDiaChi) {
        return diaChiGiaoHangHashMap.get(maDiaChi) ;
    }
    @Override
    public void add(DiaChiGiaoHang diaChiGiaoHang) {
        diaChiGiaoHangHashMap.put(diaChiGiaoHang.getMaDiaChiGiaoHang(),diaChiGiaoHang) ;
    }
}
