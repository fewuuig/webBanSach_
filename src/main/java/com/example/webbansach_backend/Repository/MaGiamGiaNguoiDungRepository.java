package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.MaGiamGia;
import com.example.webbansach_backend.Entity.MaGiamGiaNguoiDung;
import com.example.webbansach_backend.Entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaGiamGiaNguoiDungRepository extends JpaRepository<MaGiamGiaNguoiDung , Integer> {
    Optional<MaGiamGiaNguoiDung> findByMaGiamGiaAndNguoiDung(MaGiamGia maGiamGia, NguoiDung nguoiDung) ;
    Optional<MaGiamGiaNguoiDung> findByMaGiamGia_MaGiamAndNguoiDung_TenDangNhap(int maGiam , String tenDangNhap) ;
    Optional<MaGiamGiaNguoiDung> findByMaGiamGiaAndNguoiDung_TenDangNhap(MaGiamGia maGiam , String tenDangNhap) ;
}
