package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.dto.NguoiDungChatResponeDTO;
import com.example.webbansach_backend.dto.profileUser.UpdateProfileUserDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NguoiDungMapper {

    NguoiDungChatResponeDTO toDTO (NguoiDung nguoiDung) ;

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void  updateNguoiDung(UpdateProfileUserDTO updateProfileUserDTO , @MappingTarget NguoiDung nguoiDung) ;
}
