package com.example.webbansach_backend.Repository.elasticSearch;

import com.example.webbansach_backend.document.SachDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SachElasticRepository extends ElasticsearchRepository<SachDocument,Integer> {
    List<SachDocument> findByTenSachContaining(String tenSach) ;
}
