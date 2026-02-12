package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
@Entity
@Table(name = "sach_yeu_thich")
public class SachYeuThich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_sach_yeu_thich")
    private Long maSachYeuThich ;

    @Column(name = "trang_thai_yeu_thich")
    private boolean trangThaiYeuThich ;

    @ManyToOne
    @JoinColumn(name = "ma_sach" , nullable = false)
    private Sach sach;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung" , nullable = false)
    private NguoiDung nguoiDung ;
}
