package com.example.webbansach_backend.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "quyen")
public class Quyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_quyen")
    private int maQuyen ;

    @Column(name = "ten_quyen")
    private String tenQuyen ;


    @OneToMany( mappedBy = "quyen", fetch = FetchType.LAZY)
    private List<NguoiDungQuyen> nguoiDungQuyens = new ArrayList<>() ;
}
