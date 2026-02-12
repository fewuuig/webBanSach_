package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "gio_hang")
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_gio_hang")
    private int maGioHang ;

    @OneToOne(cascade = CascadeType.ALL )
    @JoinColumn(name = "ma_nguoi_dung")
    private NguoiDung nguoiDung ;

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "gioHang" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<GioHangSach> gioHangSaches = new ArrayList<>();


}
