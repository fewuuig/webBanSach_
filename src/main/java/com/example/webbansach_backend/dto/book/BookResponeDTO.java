package com.example.webbansach_backend.dto.book;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookResponeDTO {
    private int maSach ;
    private String tenSach ;
    private String tenTacGia ;

    private String moTa ;
    private double giaNiemYet ;
    private double giaBan ;
    private int soLuong ;
    private double trungBinhXepHang ;
}
