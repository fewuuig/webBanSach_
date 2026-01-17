package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.NguoiDung;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    NguoiDung findByUsername(String tenDangNhap) ;
}
