package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.dto.book.AddBookRequestDTO;
import com.example.webbansach_backend.dto.book.BookUpdateDTO;
import com.example.webbansach_backend.service.BookService;
import com.example.webbansach_backend.service.PaginateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    // log thường (debug, error)
    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    // 🔥 log performance riêng
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private BookService bookService;

    @Autowired
    private PaginateService paginateService;

    // ================= ADD =================
    @PostMapping("/add-new-book")
    public ResponseEntity<?> addNewBook(@RequestBody AddBookRequestDTO addBookRequestDTO){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
            bookService.addNewBook(tenDangNhap, addBookRequestDTO);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("POST /book/add-new-book | user={} | time={} ms",
                    tenDangNhap, time);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;
            log.error("ERROR POST /book/add-new-book | time={} ms | error={}", time, e.getMessage());
            throw e;
        }
    }

    // ================= SEARCH =================
    @GetMapping("/search/filter")
    public ResponseEntity<?> bookFilter(@RequestParam Map<String , Object> params,
                                        @PageableDefault(page = 0, size = 10) Pageable pageable){

        long start = System.currentTimeMillis();

        try {
            Object result = paginateService.searchFilter(params, pageable);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /book/search/filter | params={} | page={} | size={} | time={} ms",
                    params, pageable.getPageNumber(), pageable.getPageSize(), time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;
            log.error("ERROR GET /book/search/filter | time={} ms | error={}", time, e.getMessage());
            throw e;
        }
    }

    // ================= UPDATE =================
    @PutMapping("/update")
    public ResponseEntity<?> updateBook(@RequestBody BookUpdateDTO bookUpdateDTO){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
            bookService.updateBook(tenDangNhap, bookUpdateDTO);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("PUT /book/update | user={} | time={} ms",
                    tenDangNhap, time);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;
            log.error("ERROR PUT /book/update | time={} ms | error={}", time, e.getMessage());
            throw e;
        }
    }

    // ================= DELETE =================
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBooks(@RequestBody List<Integer> ids,
                                         @RequestParam("maTheLoai") int maTheLoai){

        long start = System.currentTimeMillis();

        try {
            String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
            bookService.deleteBook(tenDangNhap, ids, maTheLoai);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("DELETE /book/delete | ids={} | category={} | time={} ms",
                    ids, maTheLoai, time);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;
            log.error("ERROR DELETE /book/delete | time={} ms | error={}", time, e.getMessage());
            throw e;
        }
    }

    // ================= CAROUSEL =================
    @GetMapping("/book-new-carousel")
    public ResponseEntity<?> getBookNewCarousel(){

        long start = System.currentTimeMillis();

        try {


            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /book/book-new-carousel | time={} ms", time);

            return ResponseEntity.ok(bookService.bestSellerCarousel());

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;
            log.error("ERROR GET /book/book-new-carousel | time={} ms | error={}", time, e.getMessage());
            throw e;
        }
    }

    // ================= CATEGORY =================
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getBookCategory(@PathVariable("categoryId") int maTheLoai){

        long start = System.currentTimeMillis();

        try {
            Object result = bookService.getBookCategory(maTheLoai);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /book/category | categoryId={} | time={} ms",
                    maTheLoai, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;
            log.error("ERROR GET /book/category | time={} ms | error={}", time, e.getMessage());
            throw e;
        }
    }

    // ================= DELETED =================
    @GetMapping("/book-deleted/{categoryId}")
    public ResponseEntity<?> getBookDeleted(@PathVariable("categoryId") int maTheLoai){

        long start = System.currentTimeMillis();

        try {
            Object result = bookService.getBookDeleted(maTheLoai);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /book/book-deleted | categoryId={} | time={} ms",
                    maTheLoai, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;
            log.error("ERROR GET /book/book-deleted | time={} ms | error={}", time, e.getMessage());
            throw e;
        }
    }

    // ================= RESTORE =================
    @PutMapping("/restore")
    public ResponseEntity<?> reStoreBook(@RequestBody List<Integer> ids,
                                         @RequestParam("maTheLoai") int maTheLoai){

        long start = System.currentTimeMillis();

        try {
            bookService.reStoreBook(ids, maTheLoai);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("PUT /book/restore | ids={} | category={} | time={} ms",
                    ids, maTheLoai, time);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;
            log.error("ERROR PUT /book/restore | time={} ms | error={}", time, e.getMessage());
            throw e;
        }
    }
}