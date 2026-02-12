package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chi_tiet_don_hang")
public class ChiTietDonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_chi_tiet_don_hang")
    private long maChiTietDonHang ;

    @Column(name = "so_luong")
    private int soLuong ;

    @Column(name = "gia_ban")
    private double giaBan ;
    @Column(name = "tong_gia")
    private Double tongGia ;


    @ManyToOne(cascade = {
            CascadeType.DETACH,CascadeType.MERGE,
            CascadeType.PERSIST , CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_sach" , nullable = false)
    private Sach sach ;

    @ManyToOne(cascade = {
            CascadeType.DETACH,CascadeType.MERGE,
            CascadeType.PERSIST , CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_don_hang")
    private DonHang donHang ;
}
