package com.example.webbansach_backend.dto.book;

import com.example.webbansach_backend.dto.picture.HinhAnhDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class AddBookRequestDTO {
    @NotBlank(message = "tên sáchs không được bỏ trống")
    private String tenSach ;
    @NotBlank(message = "Ten tác giả không đc bỏ chống")
    private String tenTacGia ;
    private String isbn  ;
    @NotBlank(message = "mô tả sách không đc bỏ trống")
    private String moTa ;
    @Min(value = 0 , message = "giá phải >=0")
    private double giaNiemYet ;
    @Min(value =  0 , message = "giá phải >=0")
    private double giaBan ;
    @Min(value =  0 , message = "số lg>=0")
    private int soLuong ;
    List<HinhAnhDTO> hinhAnhDTOS ;
    private int maTheLoai ;
}
