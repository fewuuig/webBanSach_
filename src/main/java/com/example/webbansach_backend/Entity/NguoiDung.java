package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "da_kich_hoat")
    private boolean daKichHoat ;

    @Column(name = "ma_kich_hoat")
    private String maKichHoat ;

    @Column(name = "anh_dai_dien" , columnDefinition = "LONGTEXT")
    @Lob
    private String anhDaiDien ;
    @OneToMany(mappedBy = "nguoiDung" , fetch = FetchType.LAZY ,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST , CascadeType.REFRESH})
    private List<DanhGia> danhSachDanhGia ;

    @OneToMany(mappedBy = "nguoiDung" , fetch = FetchType.LAZY ,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SachYeuThich> danhSachSachYeuThich  = new ArrayList<>();

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


    @OneToMany(mappedBy = "nguoiDung" ,cascade ={CascadeType.DETACH , CascadeType.REFRESH , CascadeType.PERSIST , CascadeType.MERGE}, fetch = FetchType.LAZY)
    private  List<RefreshToken> refreshTokens ;

    @OneToOne(mappedBy = "nguoiDung" , cascade = CascadeType.ALL)
    private GioHang gioHang ;

    @OneToMany(mappedBy = "nguoiDung" , cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    private List<DiaChiGiaoHang> diaChiGiaoHangs = new ArrayList<>() ;

   @OneToMany( mappedBy = "nguoiDung", fetch = FetchType.LAZY , cascade = {CascadeType.DETACH , CascadeType.REFRESH , CascadeType.PERSIST , CascadeType.MERGE})
   private Set<MaGiamGiaNguoiDung> maGiamGiaNguoiDungs = new HashSet<>() ;
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
