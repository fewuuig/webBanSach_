package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room ,Integer> {
    Optional<Room> findByRoomId(int roomId) ;
    @Query("""
        SELECT r
        FROM Room r
        JOIN r.roomNguoiDungs rnd
        WHERE rnd.nguoiDung.tenDangNhap IN :tenDangNhap
        GROUP BY r.roomId
        HAVING count(DISTINCT rnd.nguoiDung.tenDangNhap) = 2
    """)
    Optional<Room> findRoomUser(@Param("tenDangNhap") List<String> tenDangNhap );
}
