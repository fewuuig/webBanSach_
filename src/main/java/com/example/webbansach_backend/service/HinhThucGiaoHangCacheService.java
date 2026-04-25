package com.example.webbansach_backend.service;


import com.example.webbansach_backend.Entity.HinhThucGiaoHang;

public interface HinhThucGiaoHangCacheService {
    HinhThucGiaoHang getHinhThucGiaoHang(int id) ;
    void add(HinhThucGiaoHang hinhThucGiaoHang);
}