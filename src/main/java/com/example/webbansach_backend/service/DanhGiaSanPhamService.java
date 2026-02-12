package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.DanhGiaResponeDTO;

import java.util.List;

public interface DanhGiaSanPhamService {
    void addDanhGiaSanPham(String tenDangNhap , String content , int maSach) ;
    List<DanhGiaResponeDTO> getDanhGiaMotQuyenSach(int maSach) ;
}
