package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.sql.Date;
import java.util.List;
@Data
@Controller
@Entity
@Table(name = "don_hang")
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_don_hang")
    private int maDonHang ;

    @Column(name = "ngay_tao")
    private Date ngayTao ;

    @Column(name = "dia_chi_mua_hang")
    private String diaChiMuaHang ;

    @Column(name = "dia_chi_nhan_hang")
    private String diaChiNhanHang ;

    @Column(name = "chi_phi_giao_hang")
    private double chiPhiGiaoHang ;

    @Column(name = "chi_phi_thanh_toan")
    private double chiPhiThanhToan ;

    @Column(name = "tong_tien")
    private double tongTien ;

    @OneToMany(mappedBy = "donHang" , fetch = FetchType.LAZY ,
            cascade = CascadeType.ALL)
    private List<ChiTietDonHang> danhSachChiTietDonHang ;

    @ManyToOne(cascade = {
            CascadeType.DETACH , CascadeType.MERGE , CascadeType.PERSIST , CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_nguoi_dung" ,nullable = false)
    private NguoiDung nguoiDung ;

    @ManyToOne(cascade = {
            CascadeType.DETACH , CascadeType.MERGE , CascadeType.PERSIST , CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_hinh_thuc_thanh_toan" , nullable = false)
    private HinhThucThanhToan hinhThucThanhToan ;

    @ManyToOne(cascade = {
            CascadeType.DETACH , CascadeType.MERGE ,
            CascadeType.PERSIST , CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_hinh_thuc_giao_hang")
    private HinhThucGiaoHang hinhThucGiaoHang ;
}
