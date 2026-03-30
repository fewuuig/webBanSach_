package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.book.AddBookRequestDTO;
import com.example.webbansach_backend.dto.book.BookResponeDTO;
import com.example.webbansach_backend.dto.book.BookUpdateDTO;

import java.util.List;

public interface BookService {
    void addNewBook(String tenDangNhap,AddBookRequestDTO addBookRequestDTO) ;
    void deleteBook(String tenDangNhap, List<Integer> ids , int maTheLoai)  ;
    void updateBook(String tenDangNhap , BookUpdateDTO bookUpdateDTO) ;
    List<BookResponeDTO> getBookCategory(int maTheLoai) ;
    List<BookResponeDTO> getSachNew();
    List<BookResponeDTO> getBookDeleted(int maTheLoai) ;
    void reStoreBook(List<Integer> ids , int maTheLoai ) ;
}
