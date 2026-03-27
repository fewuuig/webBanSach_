package com.example.webbansach_backend.dto.book;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookUpdateDTO {
    private int maSach ;
    private String tenSach ;
    private String tenTacGia ;
    private String isbn  ;
    private String moTa ;
    private double giaNiemYet ;
    private double giaBan ;
    private int soLuong ;
}
