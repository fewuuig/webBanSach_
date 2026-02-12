package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.GioHang;
import com.example.webbansach_backend.dto.AddToCartRequestDTO;
import com.example.webbansach_backend.dto.ViewCartDTO;

import java.util.List;
import java.util.Set;


public interface CartService {
    void addToCart(String tenDangNhap , AddToCartRequestDTO addToCartRequestDTO) ;
    public List<ViewCartDTO> viewCart(String tenDangNhap);
    public void updateQuantity(String tenDangNhap , int maSach , int soLuong ) ;
    void deleteBook( List<Long> danhSachSanPhamXoa) ;
    void clearBookFromCart(String tenDangNhap) ;
    GioHang checkGioHang(String tenDangNhap) ;
    List<ViewCartDTO> getSachDatTuGio( List<Long> danhSachSanPhamChon) ;
}
