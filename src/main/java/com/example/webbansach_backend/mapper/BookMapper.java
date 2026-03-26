package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.dto.book.AddBookRequestDTO;
import com.example.webbansach_backend.dto.book.BookResponeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    BookResponeDTO toDTO (Sach sach) ;
    Sach toEntity (AddBookRequestDTO addBookRequestDTO) ;

}
