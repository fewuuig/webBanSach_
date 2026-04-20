package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.DiaChiGiaoHang;

public interface DiaChiGiaoHangCacheService {
    DiaChiGiaoHang getDiaChi(int maDiaChi) ;
    void add(DiaChiGiaoHang diaChiGiaoHang);
}