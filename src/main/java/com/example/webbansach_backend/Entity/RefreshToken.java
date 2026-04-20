package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private int ma_refreshtoken ;

    @Column(nullable = false , unique = true)
    private String token  ;

    @Column(nullable = false )
    private Date ExpiryData ;
    @ManyToOne(cascade = {CascadeType.REFRESH , CascadeType.DETACH , CascadeType.PERSIST , CascadeType.MERGE})
    @JoinColumn(name = "ma_nguoi_dung")
    NguoiDung nguoiDung ;



}
