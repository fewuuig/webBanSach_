package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.book.BookResponeDTO;
import org.springframework.data.domain.Page;

public interface BookService {
    BookResponeDTO getInfoBook(int maSach) ;
    Page<BookResponeDTO> getBookKeyWordAndPageAndSize(String keyWord , int page , int size ) ;
    Page<BookResponeDTO> getBookPageAndSize( int page ,int size ) ;
    Page<BookResponeDTO> getBookCategoryAndPageAndSize(int maTheLoai , int page , int size) ;
}
