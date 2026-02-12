package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "gio_hang_sach")
public class GioHangSach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_gio_hang_sach")
    private Long maGioHangSach ;

    @Column(name = "so_luong")
    private int soLuong ;

    @ManyToOne
    @JoinColumn(name = "ma_gio_hang")
    private GioHang gioHang ;

    @ManyToOne
    @JoinColumn(name = "ma_sach" , nullable = false)
    private Sach sach ;

}
