package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.internal.bytebuddy.matcher.FilterableList;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
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

    @OneToMany( mappedBy = "nguoiDung",fetch = FetchType.LAZY , cascade = {CascadeType.MERGE , CascadeType.DETACH ,CascadeType.PERSIST ,CascadeType.REFRESH})
    private List<RoomNguoiDung> roomNguoiDungs = new ArrayList<>() ;

    @OneToMany( mappedBy = "nguoiDung", fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>() ;
    public boolean getDaKiHoat() {
        return this.daKichHoat ;
    }

    @OneToMany( mappedBy = "nguoiDung", fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private List<NguoiDungQuyen> nguoiDungQuyens = new ArrayList<>() ;
}
