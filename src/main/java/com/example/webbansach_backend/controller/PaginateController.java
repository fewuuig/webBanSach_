package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.PaginateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/books")
@RestController
public class PaginateController {
    @Autowired
    private PaginateService paginateService ;

    @GetMapping("/{maSach}")
    public ResponseEntity<?> getInfoBook(@PathVariable("maSach") int maSach){
        return ResponseEntity.ok(paginateService.getInfoBook(maSach)) ;
    }
    @GetMapping("/page-size")
    public ResponseEntity<?> getPageAndSize(
            @RequestParam("page") int page ,
            @RequestParam("size") int size
    ){
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(paginateService.getBookPageAndSize(page , size)) ;
    }
    @GetMapping("/category-page-size")
    public ResponseEntity<?> getBookCategoryAndPageAndSize(@RequestParam("maTheLoai") int maTheLoai,
                                                           @RequestParam("page") int page ,
                                                           @RequestParam("size") int size){
        return ResponseEntity.ok(paginateService.getBookCategoryAndPageAndSize(maTheLoai , page , size)) ;
    }




}
