package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.dto.book.BookResponeDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.awt.print.Book;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    BookResponeDTO toDTO (Sach sach) ;
}
