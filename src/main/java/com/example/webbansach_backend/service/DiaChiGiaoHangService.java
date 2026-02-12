package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.DiaChiGiaoHangRequestDTO;
import com.example.webbansach_backend.dto.DiaChiGiaoHangResponeDTO;

import java.util.List;

public interface DiaChiGiaoHangService {
    List<DiaChiGiaoHangResponeDTO> getDiaChiGiaoHang(String tenDangNhap ) ;
    void addDiaChiGiaoHang(String tenDangNhap , DiaChiGiaoHangRequestDTO diaChiGiaoHangRequestDTO) ;
}
