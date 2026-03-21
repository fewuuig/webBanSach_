package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.Message;
import com.example.webbansach_backend.dto.Message.MessageResponeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {
    @Mapping(target = "sender" , source = "nguoiDung.tenDangNhap")
    @Mapping(target = "timestamp" , source = "createdAt")
    MessageResponeDTO toDTO (Message message) ;
}
