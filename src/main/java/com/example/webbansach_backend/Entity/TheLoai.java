package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
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
    @OneToMany(mappedBy = "theLoai" , cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    private List<SachTheLoai> sachTheLoais = new ArrayList<>() ;

}
