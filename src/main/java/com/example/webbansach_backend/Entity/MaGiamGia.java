package com.example.webbansach_backend.Entity;

import com.example.webbansach_backend.Enum.DoiTuongApDungMa;
import com.example.webbansach_backend.Enum.LoaiMaGiamGia;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ma_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_giam")
    private int maGiam ;

    @Column(name = "ten_ma_giam_gia" ,  length = 25)
    private String tenMaGiamGia ;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau ;

    @Column(name = "ngay_het_han")
    private LocalDateTime ngayHetHan ;

    @Column(name = "so_luong")
    private int soLuong ;

    @Column(name = "so_ma_da_dung")
    private int soMaDaDung ;
    @Column(name = "giam_toi_da")
    private double giamToiDa ;

    @Column(name = "don_tu")
    private double donGiaTu ;

    @Column(name = "doi_tuong_ap_dung_ma")
    @Enumerated(EnumType.STRING)
    private DoiTuongApDungMa doiTuongApDungMa ;

    @Column(name = "ma_the_loai")
    private int maTheLoai ;


    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_ma_giam_gia")
    private TrangThaiMaGiamGia trangThaiMaGiamGia ;

    @Column(name = "gioi_han_so_luong_dung_user")
    private int gioiHanSoLuongDungUser ;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_ma_giam_gia")
    private LoaiMaGiamGia loaiMaGiamGia ;

    @OneToMany(mappedBy = "maGiamGia", fetch = FetchType.LAZY)
     private List<DonHang> donHangs  = new ArrayList<>();

    @OneToMany( mappedBy = "maGiamGia", fetch = FetchType.LAZY , cascade = {CascadeType.DETACH , CascadeType.REFRESH , CascadeType.PERSIST , CascadeType.MERGE})
    private List<MaGiamGiaNguoiDung> maGiamGiaNguoiDungs = new ArrayList<>() ;
    @OneToMany( mappedBy = "maGiamGia",fetch = FetchType.LAZY , cascade = CascadeType.ALL , orphanRemoval = true)
    List<MaGiamGiaSach> maGiamGiaSaches = new ArrayList<>() ;

}
