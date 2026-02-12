package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.WishLoveDTO;

import java.util.List;

public interface WishLoveService {
    public void addWishLoveList(String tenDangNhap, int maSach) ;
    public List<WishLoveDTO> getWishLoveList(String tenDangNhap) ;
    public boolean check(String tenDangNhap,int maSach) ;

}
