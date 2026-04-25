package com.example.webbansach_backend.Repository.customer;

import com.example.webbansach_backend.builder.BookSearchBuiler;
import com.example.webbansach_backend.dto.book.BookResponeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SachCustomRepository {
    Page<BookResponeDTO> findBookFilter(BookSearchBuiler bookSearchBuiler , Pageable pageable) ;
}
