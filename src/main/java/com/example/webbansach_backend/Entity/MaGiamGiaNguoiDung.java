package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "ma_giam_gia_nguoi_dung")
@Data
public class MaGiamGiaNguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_giam_nguoi_dung")
    private int maGiamNguoiDung ;

    @Column(name = "luot_dung_toi_da" )
    private int luotDungToiDa ;

    @Column(name = "da_dung")
    private int daDung ;

    @Column(name = "ngay_nhan")
    private LocalDateTime ngayNhan ;

    @ManyToOne
    @JoinColumn(name = "ma_giam" , nullable = false)
    private MaGiamGia maGiamGia ;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung" , nullable = false)
    private NguoiDung nguoiDung ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaGiamGiaNguoiDung that = (MaGiamGiaNguoiDung) o;
        return Objects.equals(maGiamGia.getMaGiam(), that.maGiamGia.getMaGiam())
                && Objects.equals(nguoiDung.getMaNguoiDung(), that.nguoiDung.getMaNguoiDung());
    }

    @Override
    public int hashCode() {
        return Objects.hash(maGiamGia.getMaGiam(), nguoiDung.getMaNguoiDung());
    }

}
