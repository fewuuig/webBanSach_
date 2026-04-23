package com.example.webbansach_backend.service;

import com.example.webbansach_backend.document.SachDocument;
import com.example.webbansach_backend.dto.elastic.BookDocumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

public interface ElasticSyncService {
    String syncAllBookToElastic() ;
    void syncOneBookToElastic(int maSach) ;
    void deleteBookFromElastic(int id) ;
    List<SachDocument> searchBookContaining(String tenSach) ;
    BookDocumentDTO complexSearch(double priceFrom , double priceTo , String category ) ;
    BookDocumentDTO searchComplexV2(double priceFrom , double priceTo , String category) ;
    List<SachDocument> fuzzinessSearch(String bookName);
    List<SachDocument> fuzzinessMultiMatchSearch(String bookName) ;
    List<String> autoCompleteSearch(String prefix);
    Page<SachDocument> searchPagination(Pageable pageable , String tenSach) ;
}
