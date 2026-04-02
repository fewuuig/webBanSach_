package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.img.HinhAnhResponeDTO;

import java.util.List;

public interface HinhAnhService {
    List<HinhAnhResponeDTO> getAnhCuaMotSach(int maSach , int soLuong) ;
}
