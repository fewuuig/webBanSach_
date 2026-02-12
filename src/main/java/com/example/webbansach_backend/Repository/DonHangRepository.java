package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.DonHang;
import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "don-hang")
public interface DonHangRepository extends JpaRepository<DonHang,Integer> {
    List<DonHang> findByTrangThai(TrangThaiGiaoHang trangThaiGiaoHang);
    List<DonHang> findByNguoiDung_TenDangNhapAndTrangThai(String tenDangNhap , TrangThaiGiaoHang trangThaiGiaoHang) ;
    Optional<DonHang> findByNguoiDung_TenDangNhapAndMaDonHang(String tenDangNhap , int maDonHang) ;
}
