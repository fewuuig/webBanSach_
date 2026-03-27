package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nguoidung_quyen")
@Getter
@Setter
public class NguoiDungQuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nguoi_dung_quyen")
    private int maNguoiDungQuyen ;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung")
    private NguoiDung nguoiDung ;

    @ManyToOne
    @JoinColumn(name = "ma_quyen")
    private Quyen quyen ;
}
