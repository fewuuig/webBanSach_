package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.DiaChiGiaoHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiaChiGiaoHangRepository extends JpaRepository<DiaChiGiaoHang , Integer> {
    Optional<DiaChiGiaoHang> findByNguoiDung_TenDangNhap(String tenDangNhap) ;
}
