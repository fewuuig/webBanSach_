package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "hinh_anh")
public class HinhAnh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_hinh_anh")
    private int maHinhAnh ;

    @Column(name = "ten_hinh_anh" , length = 100)
    private String tenHinhAnh ;

    @Column(name = "icon")
    private boolean icon ;

    @Column(name= "link")
    private String link ; // duongDan

    @Column(name = "du_lieu_anh" , columnDefinition = "LONGTEXT") // kiểu dữ liệu lớn
    @Lob
    private String duLieuAnh ; // base64

    @ManyToOne(cascade = {
            CascadeType.DETACH , CascadeType.MERGE ,
            CascadeType.PERSIST , CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_sach" , nullable = false)
    private Sach sach ;
}
