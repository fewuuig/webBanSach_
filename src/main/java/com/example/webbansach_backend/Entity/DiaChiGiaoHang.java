package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dia_chi_giao_hang")
public class DiaChiGiaoHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dia_chi_giao_hang")
    private int maDiaChiGiaoHang ;

    @Column(name = "tinh_or_city" , nullable = false)
    private String tinhOrCity ;
    @Column(name = "quan_or_huyen" , nullable = false)
    private String quanOrHuyen ;
    @Column(name = "phuong_or_xa" , nullable = false)
    private String phuongOrXa ;
    @Column(name = "so_nha")
    private String soNha  ;
    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung")
    private NguoiDung nguoiDung ;

}
