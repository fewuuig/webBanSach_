package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.HinhThucThanhToan;
import com.example.webbansach_backend.Repository.HinhThucThanhToanRepository;
import com.example.webbansach_backend.service.HinhThucThanhToanCacheService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HinhThucThanhToanCacheServiceImpl implements HinhThucThanhToanCacheService {
    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository ;
    private Set<Integer> ids = new HashSet<>() ;

    @PostConstruct
    public void load(){
        List<HinhThucThanhToan> hinhThucThanhToans = hinhThucThanhToanRepository.findAll() ;
        ids = hinhThucThanhToans.stream().map(HinhThucThanhToan::getMaHinhThucThanhToan).collect(Collectors.toSet());
    }
    @Override
    public boolean conatinsId(int maHinhThucThanhToan) {
        return ids.contains(maHinhThucThanhToan);
    }

    @Override
    public void add(int id) {
        ids.add(id) ;
    }
}
