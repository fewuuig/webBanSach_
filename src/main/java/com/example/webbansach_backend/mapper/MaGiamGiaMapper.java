package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.MaGiamGia;
import com.example.webbansach_backend.dto.MaGiamGiaCuaSachRespone;
import com.example.webbansach_backend.dto.MaGiamGiaRequestDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface MaGiamGiaMapper {
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE ) : khi update entity bỏ qua những fields null
//    @BeanMapping(ignoreByDefault = true) // chỉ mmap vài fields , còn lại tự cấu hình bằng source , target , constant
    @Mapping(target = "soMaDaDung" ,  constant = "0")
    @Mapping(target = "trangThaiMaGiamGia" , expression = "java(com.example.webbansach_backend.Enum.TrangThaiMaGiamGia.DANG_HOAT_DONG)")
    @Mapping(target = "maGiam" , ignore = true)
    @Mapping(target = "donHangs" , ignore = true)
    @Mapping(target = "maGiamGiaNguoiDungs" , ignore = true)
    @Mapping(target = "maGiamGiaSaches" , ignore = true)
    MaGiamGia toEntity(MaGiamGiaRequestDTO maGiamGiaRequestDTO) ;
    MaGiamGiaCuaSachRespone toDTO(MaGiamGia maGiamGia) ;
}
