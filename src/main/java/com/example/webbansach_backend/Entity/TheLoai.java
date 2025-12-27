package com.example.webbansach_backend.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
@Entity
@Table(name = "the_loai")
public class TheLoai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_the_loai")
    private int maTheLoai ;

    @Column(name = "ten_the_loai", length = 256)
    private String tenTheLoai ;

    @ManyToMany(fetch = FetchType.LAZY , cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REFRESH , CascadeType.DETACH
    })
    @JoinTable(
            name = "sach_theloai" ,
            joinColumns = @JoinColumn(name = "ma_the_loai"),
            inverseJoinColumns = @JoinColumn(name = "ma_sach")
    )
    private List<Sach> danhSachQuyenSach ;
}
