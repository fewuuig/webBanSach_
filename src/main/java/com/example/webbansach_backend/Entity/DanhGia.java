package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "danh_gia")
public class DanhGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_danh_gia")
    private Long maDanhGia ;

    @Column(name = "nhan_xet")
    private String nhanXet ;

    @Column(name = "diem_xep_hang")
    private float diemXepHang ; // 1->5

    @ManyToOne(cascade = {
            CascadeType.DETACH , CascadeType.MERGE , CascadeType.PERSIST , CascadeType.REFRESH})
    @JoinColumn(name = "ma_sach" , nullable = false)
    private Sach sach ;

    @ManyToOne(cascade = {
            CascadeType.DETACH , CascadeType.MERGE , CascadeType.PERSIST , CascadeType.REFRESH})
    @JoinColumn(name = "ma_nguoi_dung")
    private NguoiDung nguoiDung  ;
}
