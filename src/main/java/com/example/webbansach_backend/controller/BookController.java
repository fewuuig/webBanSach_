package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.book.AddBookRequestDTO;
import com.example.webbansach_backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService ;

    @PostMapping("/add-new-book")
    public ResponseEntity<?> addNewBook(@RequestBody AddBookRequestDTO addBookRequestDTO){
        bookService.addNewBook(addBookRequestDTO);
        return ResponseEntity.noContent().build() ;
    }

}
