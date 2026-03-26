package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.book.BookResponeDTO;
import org.springframework.data.domain.Page;

public interface PaginateService {
    BookResponeDTO getInfoBook(int maSach) ;
    Page<BookResponeDTO> getBookPageAndSize( int page ,int size ) ;
    Page<BookResponeDTO> getBookCategoryAndPageAndSize(int maTheLoai , int page , int size) ;
}
