package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "ma_giam_gia_sach")
@Data
public class MaGiamGiaSach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_giam_sach")
    private int maGiamSach;

    @ManyToOne
    @JoinColumn(name = "ma_sach" , nullable = false)
    private Sach sach ;

    @ManyToOne
    @JoinColumn(name = "ma_giam" , nullable = false)
    private MaGiamGia maGiamGia;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaGiamGiaSach that = (MaGiamGiaSach) o;
        return Objects.equals(sach.getMaSach(), that.sach.getMaSach())
                && Objects.equals(maGiamGia.getMaGiam(), that.maGiamGia.getMaGiam());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sach.getMaSach(), maGiamGia.getMaGiam());
    }
}
