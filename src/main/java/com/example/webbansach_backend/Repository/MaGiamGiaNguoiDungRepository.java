package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.MaGiamGiaNguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaGiamGiaNguoiDungRepository extends JpaRepository<MaGiamGiaNguoiDung , Integer> {
    Optional<MaGiamGiaNguoiDung> findByMaGiamNguoiDungAndNguoiDung_TenDangNhap(int maGiamNguoiDung , String tenDangNhap) ;
}
