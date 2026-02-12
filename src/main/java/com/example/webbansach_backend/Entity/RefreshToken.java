package com.example.webbansach_backend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
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
