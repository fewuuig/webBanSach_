package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.NguoiDung;
import org.springframework.http.ResponseEntity;

public interface AccountService {
    boolean checkUsername(String username) ;
    boolean checkEmail(String username) ;
    ResponseEntity<?> dangKyTaiKhoan(NguoiDung nguoiDung) ;
    ResponseEntity<?> kichHoatTaiKhoan(String email , String maKichHoat ) ;
    void disableAccount(String tenDangNhap , String tenDangNhapofUser) ;
}
