package com.example.webbansach_backend.service.impl;

import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.SourceFilter;
import co.elastic.clients.json.JsonData;
import com.example.webbansach_backend.Entity.TheLoai;
import com.example.webbansach_backend.Repository.SachRepository;
import com.example.webbansach_backend.Repository.elasticSearch.SachElasticRepository;
import com.example.webbansach_backend.document.SachDocument;
import com.example.webbansach_backend.dto.elastic.BookDocumentDTO;
import com.example.webbansach_backend.mapper.SachDocumentMapper;
import com.example.webbansach_backend.service.ElasticSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElasticSyncServiceImpl implements ElasticSyncService {
    @Autowired
    private SachElasticRepository sachElasticRepository ;
    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private SachDocumentMapper sachDocumentMapper ;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations ;

    // đồng bộ hét len es
    public String syncAllBookToElastic(){
        List<SachDocument> sachDocuments = sachRepository.findAll().stream().map(s->{
            SachDocument sachDocument = sachDocumentMapper.toDoc(s) ;
            List<String> theLoai = s.getDanhSachTheLoai().stream().map(TheLoai::getTenTheLoai).toList() ;
            sachDocument.setTheLoai(theLoai);
            return sachDocument ;
        }).toList() ;
        sachElasticRepository.saveAll(sachDocuments) ;
        return "Đồng bộ thành công!" ;
    }
    public void syncOneBookToElastic(int maSach){
      sachRepository.findById(maSach).ifPresent(
                s->{
                    SachDocument doc = sachDocumentMapper.toDoc(s) ;
                    List<String> theLoai = s.getDanhSachTheLoai().stream().map(TheLoai::getTenTheLoai).toList() ;
                    doc.setTheLoai(theLoai);
                    sachElasticRepository.save(doc) ;
                }
        );
    }
    public void deleteBookFromElastic(int id){
        sachElasticRepository.deleteById(id);
    }
    @Override
    public List<SachDocument> searchBookContaining(String tenSach){
        return sachElasticRepository.findByTenSachContaining(tenSach) ;
    }

    @Override
    public BookDocumentDTO complexSearch(double priceFrom , double priceTo , String category ){
        Query boolQuery = Query.of(q->q.bool(b->b
                        .filter(f->f.term(t->t.field("theLoai").value(category)))
                        .filter(f->f.range(r->r.field("giaBan").lte(JsonData.of(priceTo))
                                .gte(JsonData.of(priceFrom))))

                )
        ) ;
        // thống kê
        Aggregation aggregation = Aggregation.of(a->a.terms(t->t.field("theLoai").size(10))) ;
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(boolQuery).withAggregation("thong_ke_the_loai",aggregation).build() ;

        SearchHits<SachDocument> searchHits = elasticsearchOperations.search(nativeQuery, SachDocument.class) ;

        // tách lấy phần nội dung
        List<SachDocument> sachDocuments = searchHits.getSearchHits().stream().
                map(SearchHit::getContent).toList() ;

        // tách lấy thống kê
        Map<String , Object> stats = new HashMap<>() ;
        if(searchHits.hasAggregations() ){
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations() ;

            ElasticsearchAggregation elsAgg = aggregations.get("thong_ke_the_loai") ;
           if(elsAgg !=null ){
               Aggregate aggregate = elsAgg.aggregation().getAggregate();
               if(aggregate.isSterms()){
                   StringTermsAggregate termsAggregate = aggregate.sterms() ;
                   for(StringTermsBucket bucket : termsAggregate.buckets().array()){
                       String theLoai = bucket.key().stringValue() ;
                       long soLuong = bucket.docCount() ;
                       stats.put(theLoai , soLuong) ;
                   }
               }

           }

        }
        return new  BookDocumentDTO(sachDocuments ,stats ) ;
    }

    @Override
    // làm theo thồng kê theo thể loại , xem xem mỗi thể loaij có stat (giá cao nhất , thấp nhất , count , avg là bao nhiêu)
    public BookDocumentDTO searchComplexV2(double priceFrom , double priceTo , String category){
        // bool query
        Query boolQuery = Query.of(q->q.bool(b->b
                .filter(f->f.term(t->t.field("theLoai").value(category)))
                .filter(f->f.range(r->r.field("giaBan").gte(JsonData.of(priceFrom))
                        .lte(JsonData.of(priceTo))))
        )) ;

        // thống kê theo giá mỗi thể loại (dùng stat)
        Aggregation aggregation = Aggregation.of(agg->agg.terms(t->t.field("theLoai").size(10)).aggregations("thong_ke_gia" , a->a.stats(s->s.field("giaBan")))) ;

        // nó sẽ trả về 1 cái gọi là searchHits
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(boolQuery).withAggregation("thong_ke_the_loai" , aggregation).build() ;
        SearchHits<SachDocument> searchHits = elasticsearchOperations.search(nativeQuery , SachDocument.class) ;

        // tachs lấy sách tìm được (content ) , còn agg lấy sau
        List<SachDocument> sachDocuments = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent).toList() ;

        // tách lấy thống kê
        Map<String ,Object> stats = new HashMap<>() ;
       if(searchHits.hasAggregations()){
           ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations() ;
           ElasticsearchAggregation elsAgg = aggregations.get("thong_ke_the_loai") ;

           if(elsAgg!= null){
               Aggregate aggregate = elsAgg.aggregation().getAggregate();
               if(aggregate.isSterms()){
                   StringTermsAggregate stringTermsAggregate = aggregate.sterms() ;
                   for(StringTermsBucket bucket : stringTermsAggregate.buckets().array()){
                       String theLoai = bucket.key().stringValue() ;
                       long docCount = bucket.docCount();
                       stats.put(theLoai ,docCount) ;
                       Aggregate agg = bucket.aggregations().get("thong_ke_gia") ;
                       if(agg.isStats()){
                           StatsAggregate statsAggregate = agg.stats() ;
                           stats.put("count" , statsAggregate.count()) ;
                           stats.put("min" ,statsAggregate.min()) ;
                           stats.put("max" ,statsAggregate.max()) ;
                           stats.put("avg" ,statsAggregate.avg()) ;
                           stats.put("sum" ,statsAggregate.sum()) ;

                       }
                   }
               }
           }
       }
       return new BookDocumentDTO(sachDocuments,stats) ;
    }
    @Override
    // bây giờ sẽ tìm theo match và multimatch
    public List<SachDocument> fuzzinessSearch(String bookName){
        Query query = Query.of(q->q.match(m->m.field("tenSach").query(bookName).fuzziness("1"))) ;

        NativeQuery nativeQuery = NativeQuery.builder().withQuery(query).build();
        List<SachDocument> sachDocuments = elasticsearchOperations.search(nativeQuery, SachDocument.class).getSearchHits().stream()
                .map(SearchHit::getContent).toList() ;
        return sachDocuments ;
    }
    @Override
    public List<SachDocument> fuzzinessMultiMatchSearch(String bookName){
        List<String> fields = List.of("tenSach","tenTacGia","moTa") ;
        Query query = Query.of(q->q.multiMatch(mm->mm.query(bookName).fields(fields).fuzziness("2"))) ;

        SortOptions sortOptions = SortOptions.of(s->s.field(FieldSort.of(f->f.field("giaBan").order(SortOrder.Desc)))) ;

        HighlightParameters highlightParameters = HighlightParameters.builder()
                .withPreTags("<em class='highlight'>")
                .withPostTags("<em>")
                .build() ;

        Highlight highlight =new Highlight(highlightParameters , List.of(
                new HighlightField("moTa") ,
                new HighlightField("tenTacGia"),
                new HighlightField("tenSach"))) ;

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withHighlightQuery(new HighlightQuery(highlight,SachDocument.class))
                .withSort(sortOptions).build() ;

        SearchHits<SachDocument> searchHits = elasticsearchOperations.search(nativeQuery, SachDocument.class) ;

        // tách từng content để gắn lại highlight
        List<SachDocument> sachDocuments = searchHits.getSearchHits().stream().map(hit->{
            SachDocument sachDocument = hit.getContent() ;

            List<String> hlMoTa = hit.getHighlightField("moTa") ;
            if(!hlMoTa.isEmpty()) sachDocument.setMoTa(hlMoTa.get(0));

            List<String> hlTenTacGia = hit.getHighlightField("tenTacGia") ;
            if(!hlTenTacGia.isEmpty()) sachDocument.setTenTacGia(hlTenTacGia.get(0));

            List<String> hlTenSach = hit.getHighlightField("tenSach") ;
            if(!hlTenSach.isEmpty()) sachDocument.setTenSach(hlTenSach.get(0));

            return sachDocument ;
        }).toList() ;

        return sachDocuments ;
    }

    // use to auto complete (auto suggest) when you search
    @Override
    public List<String> autoCompleteSearch(String prefix){
        if(prefix.length() <=1) return null ;
        Query query = Query.of(q->q.matchPhrasePrefix(mpp->mpp.field("tenSach").query(prefix))) ;

        NativeQuery nativeQuery = NativeQuery.builder().withQuery(query).withSourceFilter(new FetchSourceFilter(new String[]{"tenSach"} , null)).withMaxResults(5).build() ;
        return elasticsearchOperations.search(nativeQuery,SachDocument.class).getSearchHits().stream().map(hit->{
            return  hit.getContent().getTenSach() ;
        }).toList() ;
    }

}
