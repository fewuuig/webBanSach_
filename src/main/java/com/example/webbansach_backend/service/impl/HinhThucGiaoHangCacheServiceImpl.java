package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.HinhThucGiaoHang;
import com.example.webbansach_backend.Repository.HinhThucGiaoHangRepository;
import com.example.webbansach_backend.service.HinhThucGiaoHangCacheService;
import com.example.webbansach_backend.service.HinhThucGiaoHangService;
import com.example.webbansach_backend.service.HinhThucThanhToanCacheService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HinhThucGiaoHangCacheServiceImpl implements HinhThucGiaoHangCacheService {
    @Autowired
    private HinhThucGiaoHangRepository hinhThucGiaoHangRepository;
    private Map<Integer , HinhThucGiaoHang> hinhThucGiaoHangMap = new HashMap<>();

    //
    @PostConstruct
    public void loadId(){
        List<HinhThucGiaoHang> hinhThucGiaoHangs = hinhThucGiaoHangRepository.findAll() ;
        hinhThucGiaoHangMap = hinhThucGiaoHangs.stream().collect(Collectors.toMap(HinhThucGiaoHang::getMaHinhThucGiaoHang ,s->s));
    }

    @Override
    public HinhThucGiaoHang getHinhThucGiaoHang(int id) {
        return hinhThucGiaoHangMap.get(id);
    }

    @Override
    public void add(HinhThucGiaoHang hinhThucGiaoHang) {
        hinhThucGiaoHangMap.put(hinhThucGiaoHang.getMaHinhThucGiaoHang(),hinhThucGiaoHang) ;
    }
}
