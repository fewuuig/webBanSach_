package com.example.webbansach_backend.controller;

import com.example.webbansach_backend.Repository.elasticSearch.SachElasticRepository;
import com.example.webbansach_backend.service.ElasticSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/elastic")
public class ElasticController {
    @Autowired
    private ElasticSyncService elasticSyncService ;

    @PostMapping("/sync/all-books")
    public ResponseEntity<?> syncAllBook(){
        return ResponseEntity.ok(elasticSyncService.syncAllBookToElastic()) ;
    }
    @GetMapping("/search/books")
    public ResponseEntity<?> searchBookContaining(@RequestParam("tenSach") String tenSach){
        return ResponseEntity.ok(elasticSyncService.searchBookContaining(tenSach)) ;
    }
    @GetMapping("/search/complex/book")
    public ResponseEntity<?> searchComplex(@RequestParam("priceFrom") double priceFrom ,
                                           @RequestParam("priceTo") double priceTo ,
                                           @RequestParam("theLoai") String theLoai){

        return ResponseEntity.ok(elasticSyncService.searchComplexV2(priceFrom ,priceTo,theLoai)) ;
    }
    @GetMapping("/search/fuzziness/book")
    public ResponseEntity<?> fuzziSearch(@RequestParam("bookName") String bookName){
        return ResponseEntity.ok(elasticSyncService.fuzzinessSearch(bookName)) ;
    }
    @GetMapping("/search/fuzziness/multi-match/book")
    public ResponseEntity<?> fuzziMultiMatchSearch(@RequestParam("bookName") String bookName){
        return ResponseEntity.ok(elasticSyncService.fuzzinessMultiMatchSearch(bookName)) ;
    }
    @GetMapping("/search/auto-complete")
    public ResponseEntity<?> autoCompleteSearch(@RequestParam("prefix") String prefix){
        return ResponseEntity.ok(elasticSyncService.autoCompleteSearch(prefix)) ;
    }
    @GetMapping("/search/paginate")
    public ResponseEntity<?> paginateSearch(@RequestParam("tenSach") String tenSach  ,
                                            @PageableDefault(size = 10 , page = 0)Pageable pageable){
        return ResponseEntity.ok(elasticSyncService.searchPagination(pageable , tenSach))  ; 
    }
}
