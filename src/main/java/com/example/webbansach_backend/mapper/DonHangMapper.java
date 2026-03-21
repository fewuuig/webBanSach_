package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.DonHang;
import com.example.webbansach_backend.dto.DonHangTrangThaiResponeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonHangMapper {
    @Mapping(target = "sachTrongDonDTOS" , source = "danhSachChiTietDonHang")

    DonHangTrangThaiResponeDTO toDTO(DonHang donHang) ;
}
