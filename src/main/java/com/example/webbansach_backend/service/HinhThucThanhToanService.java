package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.HinhThucThanhToan;
import com.example.webbansach_backend.dto.HinhThucThanhToanResponeDTO;

import java.util.List;

public interface HinhThucThanhToanService {
    List<HinhThucThanhToanResponeDTO> getHinhThucThanhToans() ;
}
