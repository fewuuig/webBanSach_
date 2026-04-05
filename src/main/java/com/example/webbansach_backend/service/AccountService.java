package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.dto.account.ChangePassword;
import org.springframework.http.ResponseEntity;

public interface AccountService {
    void changePassword(String tenDangNhap, ChangePassword changePassword) ;
    boolean checkUsername(String username) ;
    boolean checkEmail(String username) ;
    ResponseEntity<?> dangKyTaiKhoan(NguoiDung nguoiDung) ;
    ResponseEntity<?> kichHoatTaiKhoan(String email , String maKichHoat ) ;
    void disableAccount(String tenDangNhap , String tenDangNhapofUser) ;
}
