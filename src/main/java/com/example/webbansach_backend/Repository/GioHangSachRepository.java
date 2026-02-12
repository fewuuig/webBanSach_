package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.GioHangSach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GioHangSachRepository extends JpaRepository<GioHangSach,Long> {
//    Optional<GioHangSach> findByGioHang_MaGioHangAndSach_MaSach(int maGioHang , int maSach) ;
    Optional<GioHangSach> findBySach_MaSach(int maSach) ;
    void deleteByGioHang_MaGioHangAndSach_MaSachIn( int maGioHang, Set<Integer> maSach) ;
    Optional<GioHangSach> findByGioHang_NguoiDung_TenDangNhapAndSach_MaSach(String tenDangNhap , int maSach) ;
    void deleteByGioHang_NguoiDung_TenDangNhapAndSach_MaSachIn(String tenDangNhap ,List<Integer> danhSachSanPhamXoa) ;
    void deleteByMaGioHangSachIn(List<Long> danhSachSanPhamChon) ;
    void deleteByMaGioHangSachIn(Set<Long> danhSachSanPhamChon) ;
    List<GioHangSach> findByMaGioHangSachIn(List<Long> danhSachSanPhamChon) ;
}
