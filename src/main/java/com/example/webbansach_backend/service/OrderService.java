package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import com.example.webbansach_backend.dto.DatHangRequestDTO;
import com.example.webbansach_backend.dto.DonHangTrangThaiResponeDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.redis.connection.stream.MapRecord;

import java.util.List;

public interface OrderService {
    public void placeOder( String tenDangNhap , DatHangRequestDTO datHangRequestDTO) throws JsonProcessingException;
    void capNhatTrangThaiDonHang( String tenDangNhap,Long maDonHang, TrangThaiGiaoHang trangThai) ;
    List<DonHangTrangThaiResponeDTO> getDonHangTheoTrangThai( String tenDangNhap,TrangThaiGiaoHang trangThaiGiaoHang);
    void thaoTacDonHang(String tenDangNhap , int maDonHang) ;
    void saveBatch(List<MapRecord<String , Object , Object>> messages , int shard) throws JsonProcessingException;
    void compensateStockRedisBook(int shard) throws JsonProcessingException ;

}
