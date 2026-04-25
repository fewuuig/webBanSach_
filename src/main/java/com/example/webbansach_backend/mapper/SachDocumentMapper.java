package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.document.SachDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SachDocumentMapper {
    @Mapping(target = "theLoai" , ignore = true)
    SachDocument toDoc(Sach sach) ;
}
