package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.MaGiamGia;
import com.example.webbansach_backend.dto.*;
import com.example.webbansach_backend.dto.voucher.UpdateVoucherDTO;

import java.util.List;

public interface MaGiamGiaService {
    List<MaGiamGiaResponeDTO> getMaGiamGiaCuaSach(List<Integer> danhSachMaSach) ;
    MaGiamGiaUserResponeDTO getMaGiamGiaCuaNguoiDung(String tenDangNhap ) ;
    void capNhatMaGiamGiaSach(int maGiam , UpdateMaGiamGiaDTO updateMaGiamGiaDTO) ;
    void themMaGiamGia(MaGiamGiaRequestDTO maGiamGiaRequestDTO) ;
    void dungMaGiamGiaUser(String tenDangNhap , int maGiam , String request_id) ;
    MaGiamGia getMaGiamGia(int maGiam) ;
    List<MaGiamGiaCuaUserResponeDTO> getAllVoucher();
    void updateVoucher(UpdateVoucherDTO updateVoucherDTO , int maGiam) ;
}
