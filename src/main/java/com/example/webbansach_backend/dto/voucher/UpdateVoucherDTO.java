package com.example.webbansach_backend.dto.voucher;

import com.example.webbansach_backend.Enum.DoiTuongApDungMa;
import com.example.webbansach_backend.Enum.LoaiMaGiamGia;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class UpdateVoucherDTO {
    private String tenMaGiamGia ;
    private LocalDateTime ngayBatDau ;
    private LocalDateTime ngayHetHan ;
    private int soLuong ;
    private int soMaDaDung ;
    private Double giamToiDa ;
    private Double donGiaTu ;
    private DoiTuongApDungMa doiTuongApDungMa ;
    private Double phanTramGiam ;
    private Double tienGiam ;
    private TrangThaiMaGiamGia trangThaiMaGiamGia ;
    private int gioiHanSoLuongDungUser ;
    private LoaiMaGiamGia loaiMaGiamGia ;

}
