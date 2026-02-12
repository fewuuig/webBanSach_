package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.HinhThucGiaoHangResponeDTO;
import com.example.webbansach_backend.dto.HinhThucThanhToanResponeDTO;

import java.util.List;

public interface HinhThucGiaoHangService {
    List<HinhThucGiaoHangResponeDTO> getHinhThucGiaoHang() ;
}
