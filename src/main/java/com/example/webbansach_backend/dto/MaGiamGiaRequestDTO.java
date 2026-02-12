package com.example.webbansach_backend.dto;

import com.example.webbansach_backend.Enum.DoiTuongApDungMa;
import com.example.webbansach_backend.Enum.LoaiMaGiamGia;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MaGiamGiaRequestDTO {
    private String tenMaGiamGia ;
    private LocalDateTime ngayBatDau ;
    private LocalDateTime ngayHetHan ;
    private int soLuong ;
    private double giamToiDa ;
    private double donGiaTu ;
    private LoaiMaGiamGia loaiMaGiamGia ;
    private DoiTuongApDungMa doiTuongApDungMa ; // đối tượng áp dụng cái mã giảm gía này
    private int gioiHanSoLuongDungUser ; // cqis này là giới hạn lượt dùng đối vơí trường hợp là mã dành cho user
    private Integer maTheLoai ; // mã áp dụng cho thể loai nào
    private List<Integer> danhSachMaSach ; // đối với đối tương áp dụng là sách

}
