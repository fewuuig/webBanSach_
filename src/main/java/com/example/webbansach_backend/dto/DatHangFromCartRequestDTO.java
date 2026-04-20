package com.example.webbansach_backend.dto;

import lombok.Data;

import java.util.List;
@Data
public class DatHangFromCartRequestDTO {
    private List<OrderItem> items ;
    private int maDiaChiGiaoHang;
    private int maHinhThucThanhToan ;
    private int maHinhThucGiaoHang ;

}
//class OrderItem{
//    private int maSach ;
//    private int soLuong ;
//}
