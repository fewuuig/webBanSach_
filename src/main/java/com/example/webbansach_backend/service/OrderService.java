package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.DonHang;
import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import com.example.webbansach_backend.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface OrderService {
    public void placeOder( String tenDangNhap , DatHangRequestDTO datHangRequestDTO) throws JsonProcessingException;
    void capNhatTrangThaiDonHang(int maDonHang, TrangThaiGiaoHang trangThai) ;
    List<DonHangTrangThaiResponeDTO> getDonHangTheoTrangThai( String tenDangNhap,TrangThaiGiaoHang trangThaiGiaoHang);
    void thaoTacDonHang(String tenDangNhap , int maDonHang) ;

}
