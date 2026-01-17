package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.ClientInfoStatus;
import java.util.List;
@Data
@Entity
@Table(name = "nguoi_dung")
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nguoi_dung" )
    private int maNguoiDung ;

    @Column(name = "ho_dem" , length = 30  )
    private  String hoDem ;

    @Column(name = "ten" , length = 30)
    private String ten ;

    @Column(name = "ten_dang_nhap",length = 100 )
    private String tenDangNhap ;

    @Column(name = "mat_khau" , length = 512)
    private String matKhau ;

    @Column(name = "gioi_tinh")
    private  char gioiTinh ;

    @Column(name = "email" , length = 256 )
    private String email ;

    @Column(name = "so_dien_thoai" , length = 12)
    private String soDienThoai ;

    @Column(name = "dia_chi_mua_hang")
    private String diaChiMuaHang ;

    @Column(name = "dia_chi_giao_hang")
    private String diaChiGiaoHang ;

    @Column(name = "da_kich_hoat")
    private boolean daKichHoat ;

    @Column(name = "ma_kich_hoat")
    private String maKichHoat ;
    @OneToMany(mappedBy = "nguoiDung" , fetch = FetchType.LAZY ,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST , CascadeType.REFRESH})
    private List<DanhGia> danhSachDanhGia ;

    @OneToMany(mappedBy = "nguoiDung" , fetch = FetchType.LAZY ,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST , CascadeType.REFRESH})
    private List<SachYeuThich> danhSachSachYeuThich ;

    @ManyToMany(fetch = FetchType.LAZY , cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST , CascadeType.REFRESH})
    @JoinTable(
            name = "nguoidung_quyen",
            joinColumns = @JoinColumn(name = "ma_nguoi_dung"),
            inverseJoinColumns = @JoinColumn(name = "ma_quyen")
    )
    private List<Quyen> danhSachQuyen ;

    @OneToMany(mappedBy = "nguoiDung" , fetch = FetchType.LAZY ,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST , CascadeType.REFRESH})
    private List<DonHang> danhSachDonHang ;


    @Override
    public String toString() {
        return "NguoiDung{" +
                "maNguoiDung=" + maNguoiDung +
                ", hoDem='" + hoDem + '\'' +
                ", ten='" + ten + '\'' +
                ", tenDangNhap='" + tenDangNhap + '\'' +
                ", matKhau='" + matKhau + '\'' +
                ", gioiTinh=" + gioiTinh +
                ", email='" + email + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", diaChiMuaHang='" + diaChiMuaHang + '\'' +
                ", diaChiGiaoHang='" + diaChiGiaoHang + '\'' +
                ", daKichHoat=" + daKichHoat +
                ", ma_kich_hoat='" + maKichHoat + '\'' +
                ", danhSachDanhGia=" + danhSachDanhGia +
                ", danhSachSachYeuThich=" + danhSachSachYeuThich +
                ", danhSachQuyen=" + danhSachQuyen +
                ", danhSachDonHang=" + danhSachDonHang +
                '}';
    }

    public boolean getDaKiHoat() {
        return this.daKichHoat ;
    }
}
