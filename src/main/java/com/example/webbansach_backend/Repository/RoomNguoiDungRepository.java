package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.RoomNguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomNguoiDungRepository extends JpaRepository<RoomNguoiDung , Integer> {
    boolean existsByNguoiDung_TenDangNhapIn(List<String> tenDangNhap ) ;
    boolean existsByNguoiDung_TenDangNhapAndRoom_RoomId(String tenDangNhap , int roomId) ;

}
