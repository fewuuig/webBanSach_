package com.example.webbansach_backend.Entity;

import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Controller;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
@Entity
@Table(name = "don_hang")
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_don_hang")
    private int maDonHang ;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao ;

    @Enumerated(EnumType.STRING) // kiểu enumType.String giúp lưu String vào cột thôi k lưu enum vào DB
    @Column(name ="trang_thai"  , columnDefinition = "Vachar(50)")
    private TrangThaiGiaoHang trangThai ;

    @Column(name = "chi_phi_giao_hang")
    private double chiPhiGiaoHang ;

    @Column(name = "dia_chi_nhan_hang")
    private String diaChiNhanHang ;

    @OneToMany(mappedBy = "donHang" , fetch = FetchType.LAZY ,
            cascade = CascadeType.ALL)
    private List<ChiTietDonHang> danhSachChiTietDonHang = new ArrayList<>();

    @ManyToOne(cascade = {
            CascadeType.DETACH , CascadeType.MERGE , CascadeType.PERSIST , CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_nguoi_dung" ,nullable = true)
    private NguoiDung nguoiDung ;

    @ManyToOne
    @JoinColumn(name = "ma_hinh_thuc_thanh_toan" , nullable = false)
    private HinhThucThanhToan hinhThucThanhToan ;

    @ManyToOne(cascade = {
            CascadeType.DETACH , CascadeType.MERGE ,
            CascadeType.PERSIST , CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_hinh_thuc_giao_hang")
    private HinhThucGiaoHang hinhThucGiaoHang ;
    @ManyToOne
    @JoinColumn( name = "ma_giam", nullable = true)
    private MaGiamGia maGiamGia ;
}
