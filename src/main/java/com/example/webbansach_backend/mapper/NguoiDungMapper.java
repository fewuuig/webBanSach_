package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.dto.NguoiDungChatResponeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NguoiDungMapper {

    NguoiDungChatResponeDTO toDTO (NguoiDung nguoiDung) ;
}
