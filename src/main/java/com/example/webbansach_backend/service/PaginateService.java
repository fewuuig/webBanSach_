package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.book.BookResponeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PaginateService {
    BookResponeDTO getInfoBook(int maSach) ;
    Page<BookResponeDTO> getBookPageAndSize( int page ,int size ) ;
    Page<BookResponeDTO> getBookCategoryAndPageAndSize(int maTheLoai , int page , int size) ;
    Page<BookResponeDTO> searchFilter(Map<String , Object> params, Pageable pageable);
}
