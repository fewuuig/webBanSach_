package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Repository.customer.SachCustomRepository;
import com.example.webbansach_backend.dto.book.AddBookRequestDTO;
import com.example.webbansach_backend.dto.book.BookUpdateDTO;
import com.example.webbansach_backend.service.BookService;
import com.example.webbansach_backend.service.PaginateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService ;
    @Autowired
    private PaginateService paginateService ;

    @PostMapping("/add-new-book")
    public ResponseEntity<?> addNewBook(@RequestBody AddBookRequestDTO addBookRequestDTO){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        bookService.addNewBook( tenDangNhap,addBookRequestDTO);
        return ResponseEntity.noContent().build() ;
    }
    @GetMapping("/search/filter")
    public ResponseEntity<?> bookFilter(@RequestParam Map<String , Object> params ,
                                        @PageableDefault(page= 0 , size =10) Pageable pageable){
        return ResponseEntity.ok(paginateService.searchFilter(params ,pageable)) ;
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateBook(@RequestBody BookUpdateDTO bookUpdateDTO){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        bookService.updateBook(tenDangNhap , bookUpdateDTO);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBooks(@RequestBody List<Integer> ids , @RequestParam("maTheLoai") int maTheLoai){
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName() ;
        bookService.deleteBook(tenDangNhap ,ids , maTheLoai);
        return ResponseEntity.noContent().build() ;
    }
    @GetMapping("/book-new-carousel")
    public ResponseEntity<?> getBookNewCarousel(){
        return ResponseEntity.ok(bookService.getSachNew()) ;
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getBookCategory(@PathVariable("categoryId") int maTheLoai){
        return ResponseEntity.ok(bookService.getBookCategory(maTheLoai)) ;
    }
}
