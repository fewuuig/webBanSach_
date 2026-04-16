package com.example.webbansach_backend.dto.book;

import com.example.webbansach_backend.dto.picture.HinhAnhDTO;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

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
    List<HinhAnhDTO> hinhAnhDTOS ;
    Set<Integer> idImgDelete ;
}
