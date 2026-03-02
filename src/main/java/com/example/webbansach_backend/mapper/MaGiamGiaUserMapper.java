package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.MaGiamGia;
import com.example.webbansach_backend.dto.MaGiamGiaCuaUserResponeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring"  , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MaGiamGiaUserMapper {
    @Mapping(target = "maGiamNguoiDung" , ignore = true)
    @Mapping(target = "daDung", ignore = true)
    MaGiamGiaCuaUserResponeDTO toDto (MaGiamGia maGiamGia) ;
}
