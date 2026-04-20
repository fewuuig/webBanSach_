package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.HinhThucThanhToan;

public interface HinhThucThanhToanCacheService {
    boolean conatinsId(int maHinhThucThanhToan) ;
    void add(int id);
}