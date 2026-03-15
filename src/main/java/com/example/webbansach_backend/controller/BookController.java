package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.BookService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/books")
@RestController
public class BookController {
    @Autowired
    private BookService bookService ;

    @GetMapping("/{maSach}")
    public ResponseEntity<?> getInfoBook(@PathVariable("maSach") int maSach){
        return ResponseEntity.ok(bookService.getInfoBook(maSach)) ;
    }
    @GetMapping("/keyword-page-size")
    public ResponseEntity<?> getPageBook(@RequestParam("keyWord") String keyWord,
                                        @RequestParam("page") int page ,
                                        @RequestParam("size") int size){
        return ResponseEntity.ok(bookService.getBookKeyWordAndPageAndSize(keyWord, page, size)) ;
    }
    @GetMapping("/page-size")
    public ResponseEntity<?> getPageAndSize(
            @RequestParam("page") int page ,
            @RequestParam("size") int size
    ){
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(bookService.getBookPageAndSize(page , size)) ;
    }
    @GetMapping("/category-page-size")
    public ResponseEntity<?> getBookCategoryAndPageAndSize(@RequestParam("maTheLoai") int maTheLoai,
                                                           @RequestParam("page") int page ,
                                                           @RequestParam("size") int size){
        return ResponseEntity.ok(bookService.getBookCategoryAndPageAndSize(maTheLoai , page , size)) ;
    }




}
