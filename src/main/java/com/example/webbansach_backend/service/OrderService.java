package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.DonHang;
import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import com.example.webbansach_backend.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface OrderService {
    void placeOrderFromCart(String tenDangNhap , DatHangFromCartRequestDTO datHangFromCartRequestDTO) ;
    public void placeOder(String tenDangNhap , DatHangRequestDTO datHangRequestDTO) ;
    void capNhatTrangThaiDonHang(int maDonHang, TrangThaiGiaoHang trangThai) ;
    List<DonHangTrangThaiResponeDTO> getDonHangTheoTrangThai( String tenDangNhap,TrangThaiGiaoHang trangThaiGiaoHang);
    void thaoTacDonHang(String tenDangNhap , int maDonHang) ;
}
