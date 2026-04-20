package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.DiaChiGiaoHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DiaChiGiaoHangRepository extends JpaRepository<DiaChiGiaoHang , Integer> {
    Optional<DiaChiGiaoHang> findByNguoiDung_TenDangNhap(String tenDangNhap) ;
    List<DiaChiGiaoHang> findByMaDiaChiGiaoHangIn(Set<Integer> ids) ;
}
