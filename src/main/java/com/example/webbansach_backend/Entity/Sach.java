package com.example.webbansach_backend.Entity;


import jakarta.persistence.*;
import lombok.Data;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "sach")
public class Sach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ma_sach")
    private int maSach ;

    @Column(name = "ten_sach" , length = 256)
    private String tenSach ;

    @Column(name ="ten_tac_gia" , length = 256)
    private String tenTacGia ;

    @Column(name="isbn" , length = 256)
    private String ISBN  ;

    @Column(name="mo_ta" , columnDefinition = "TEXT")
    private String moTa ;

    @Column(name="gia_niem_yet")
    private double giaNiemYet ;

    @Column(name = "gia_ban")
    private double giaBan ;

    @Column(name = "so_luong")
    private int soLuong ;

    @Column(name="trung_binh_xep_hang")
    private double trungBinhXepHang ;

    @ManyToMany(fetch = FetchType.LAZY , cascade = {
            CascadeType.PERSIST , CascadeType.MERGE,
            CascadeType.DETACH , CascadeType.REFRESH
    })
    @JoinTable(
            name = "sach_theloai",
            joinColumns = @JoinColumn(name = "ma_sach") ,
            inverseJoinColumns = @JoinColumn(name = "ma_the_loai")

    )
    private List<TheLoai> danhSachTheLoai ;

    //Đoan nay coi nhu cascade.All
    @OneToMany(mappedBy = "sach" ,fetch = FetchType.LAZY , cascade = {
            CascadeType.DETACH ,CascadeType.MERGE ,
            CascadeType.PERSIST , CascadeType.REFRESH,CascadeType.REMOVE
    })
    private List<HinhAnh> danhSachHinhAnh ;

    @OneToMany(mappedBy = "sach" ,fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private List<DanhGia> danhSachDanhGia ;

    @OneToMany(mappedBy = "sach" ,fetch = FetchType.LAZY , cascade = {
            CascadeType.DETACH ,CascadeType.MERGE , CascadeType.PERSIST , CascadeType.REFRESH
    })
    private List<ChiTietDonHang> danhSachChiTietDonHang  ;

    @OneToMany(mappedBy = "sach" ,fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private List<SachYeuThich> danhSachSachYeuThich ;

    @OneToMany(mappedBy = "sach" , fetch = FetchType.LAZY )
    private List<GioHangSach> gioHangSaches ;
    @OneToMany(mappedBy = "sach", cascade = CascadeType.ALL)
    Set<MaGiamGiaSach> maGiamGiaSaches = new HashSet<>();

}
