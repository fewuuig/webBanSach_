package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.MaGiamGiaRequestDTO;
import com.example.webbansach_backend.dto.MaGiamGiaResponeDTO;
import com.example.webbansach_backend.dto.MaGiamGiaUserResponeDTO;
import com.example.webbansach_backend.dto.UpdateMaGiamGiaDTO;

import java.util.List;

public interface MaGiamGiaService {
    List<MaGiamGiaResponeDTO> getMaGiamGiaCuaSach(List<Integer> danhSachMaSach) ;
    MaGiamGiaUserResponeDTO getMaGiamGiaCuaNguoiDung(String tenDangNhap ) ;
    void capNhatMaGiamGiaSach(int maGiam , UpdateMaGiamGiaDTO updateMaGiamGiaDTO) ;
    void themMaGiamGia(MaGiamGiaRequestDTO maGiamGiaRequestDTO) ;
}
