package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sach_theloai")
@Getter
@Setter
public class SachTheLoai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_sach_the_loai")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ma_sach")
    private Sach sach ;

    @ManyToOne
    @JoinColumn(name = "ma_the_loai")
    private TheLoai theLoai ;
}
