package com.example.webbansach_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeleteBookToCartRequestDTO {
    private List<Long> danhSachSanPhamChon ;

}
