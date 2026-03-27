package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.Room;
import com.example.webbansach_backend.Entity.RoomNguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomNguoiDungRepository extends JpaRepository<RoomNguoiDung , Integer> {
    boolean existsByNguoiDung_TenDangNhapIn(List<String> tenDangNhap ) ;
    boolean existsByNguoiDung_TenDangNhapAndRoom_RoomId(String tenDangNhap , int roomId) ;

}
