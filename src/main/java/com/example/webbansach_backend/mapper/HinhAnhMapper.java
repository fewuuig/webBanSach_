package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.HinhAnh;
import com.example.webbansach_backend.dto.img.HinhAnhResponeDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HinhAnhMapper {
    HinhAnhResponeDTO toDTO(HinhAnh hinhAnh) ;

}
