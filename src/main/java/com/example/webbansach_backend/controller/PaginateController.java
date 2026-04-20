package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.service.PaginateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class PaginateController {

    // log thường
    private static final Logger log = LoggerFactory.getLogger(PaginateController.class);

    // log performance riêng (QUAN TRỌNG)
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF_LOGGER");

    @Autowired
    private PaginateService paginateService;


    @GetMapping("/{maSach}")
    public ResponseEntity<?> getInfoBook(@PathVariable("maSach") int maSach){

        long start = System.currentTimeMillis();

        try {
            Object result = paginateService.getInfoBook(maSach);

            long time = System.currentTimeMillis() - start;

            // 👉 log performance
            perfLogger.info("GET /books/{} | time={} ms", maSach, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /books/{} | time={} ms | error={}", maSach, time, e.getMessage());

            throw e;
        }
    }


    @GetMapping("/page-size")
    public ResponseEntity<?> getPageAndSize(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ){

        long start = System.currentTimeMillis();

        try {
            String user = SecurityContextHolder.getContext().getAuthentication().getName();

            Object result = paginateService.getBookPageAndSize(page, size);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /books/page-size | page={} | size={} | user={} | time={} ms",
                    page, size, user, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /books/page-size | page={} | size={} | time={} ms | error={}",
                    page, size, time, e.getMessage());

            throw e;
        }
    }


    @GetMapping("/category-page-size")
    public ResponseEntity<?> getBookCategoryAndPageAndSize(
            @RequestParam("maTheLoai") int maTheLoai,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ){

        long start = System.currentTimeMillis();

        try {
            Object result = paginateService.getBookCategoryAndPageAndSize(maTheLoai, page, size);

            long time = System.currentTimeMillis() - start;

            perfLogger.info("GET /books/category-page-size | categoryId={} | page={} | size={} | time={} ms",
                    maTheLoai, page, size, time);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            log.error("ERROR GET /books/category-page-size | categoryId={} | page={} | size={} | time={} ms | error={}",
                    maTheLoai, page, size, time, e.getMessage());

            throw e;
        }
    }
}