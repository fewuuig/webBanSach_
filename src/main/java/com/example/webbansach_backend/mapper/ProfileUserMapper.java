package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.dto.ProfileUserResponeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfileUserMapper {
   ProfileUserResponeDTO toDTO (NguoiDung nguoiDung) ;
}
