package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.dto.book.AddBookRequestDTO;
import com.example.webbansach_backend.dto.book.BookResponeDTO;
import com.example.webbansach_backend.dto.book.BookUpdateDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    BookResponeDTO toDTO (Sach sach) ;
    Sach toEntity (AddBookRequestDTO addBookRequestDTO) ;

    // cập nhật entity từ DTO  : MappingTarrget + ignore null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDTO(BookUpdateDTO bookUpdateDTO , @MappingTarget Sach sach) ;
}
