package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.GioHang;
import com.example.webbansach_backend.Entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GioHangRepository extends JpaRepository<GioHang , Integer> {

    Optional<GioHang> findByNguoiDung(NguoiDung nguoiDung) ;
    Optional<GioHang> findByNguoiDung_TenDangNhap(String tenDangNhap) ;

}
