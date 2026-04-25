package com.example.webbansach_backend.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.search.RescoreQuery;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.search.Rescore;
import co.elastic.clients.json.JsonData;
import com.example.webbansach_backend.Entity.TheLoai;
import com.example.webbansach_backend.Repository.SachRepository;
import com.example.webbansach_backend.Repository.elasticSearch.SachElasticRepository;
import com.example.webbansach_backend.document.SachDocument;
import com.example.webbansach_backend.dto.elastic.BookDocumentDTO;
import com.example.webbansach_backend.mapper.SachDocumentMapper;
import com.example.webbansach_backend.service.ElasticSyncService;
import com.example.webbansach_backend.utils.StringHelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.RescorerQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private RedisTemplate<String,Object> redisTemplate ;
    @Autowired
    private StringRedisTemplate stringRedisTemplate ;
    @Autowired
    @Qualifier("autoCompleteSearch")
    private DefaultRedisScript<Long> autoCompleteSearch ;

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
    // chỗ này cần cải thiện thêm , nếu gọi trực tiếp có thể ảnh hưởng đến hiệu năng -> nên async đoạn này (driven-event)
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
    public Set<String> autoCompleteSearch(String prefix){
        if(prefix.length() <=1) return null ;
        // cache
        String key = "auto-complete-search:"+StringHelperUtil.removeAccents(prefix) ;
        List<String> cached =  stringRedisTemplate.opsForList().range( key, 0 , -1) ;
        if(cached != null && !cached.isEmpty()){
            return cached.stream().map(String::toString).collect(Collectors.toSet());
        }
        // fallback
        Query query = Query.of(q->q.matchPhrasePrefix(mpp->mpp.field("tenSach").query(prefix))) ;

        NativeQuery nativeQuery = NativeQuery.builder().withQuery(query).withMaxResults(5).withSourceFilter(new FetchSourceFilter(new String[]{"tenSach"} , null)).withMaxResults(5).build() ;

        List<String> keyHits =  elasticsearchOperations.search(nativeQuery,SachDocument.class).getSearchHits().stream().map(hit->{
            return  hit.getContent().getTenSach() ;
        }).toList() ;

        Set<String> keyHitFinal = new HashSet<>(keyHits);
        if(!keyHits.isEmpty()){
           stringRedisTemplate.execute(autoCompleteSearch ,List.of(key) , keyHitFinal.toArray(new Object[0]));
        }

        return keyHitFinal;
    }

    // phân trang
    @Override
    public Page<SachDocument> searchPagination(Pageable pageable , String tenSach){
        Query query = Query.of(q->q.match(m->m.field("tenSach").query(tenSach).fuzziness("1"))) ;
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(query).withPageable(pageable).build() ;
        SearchHits<SachDocument> searchHits = elasticsearchOperations.search(nativeQuery , SachDocument.class) ;
        List<SachDocument> sachDocuments =searchHits.stream().map(SearchHit::getContent).toList() ;

        return new PageImpl<>(sachDocuments , pageable , searchHits.getTotalHits()) ;
    }
    // hãy dựa trên thư viện elasticsearch client , spring data elasticserahc đer phát triển tiếp____
    // như : Function score query (tìm kiếm dưạ trên lượt mua/ rating )
    // như : gợi tý từu khóa hot (trending searchs) + redis (zset)

    // -> mục tiêu boost : chấm điẻm theo lượt mua / rating : scorefinal = score(match)*score(func)
    // log1p : giúp rút ngắn khoảng cách điểm số với các cuốn bán só lượng ít hơn => vẫn đc đề xuất

    // tìm theo cái nào có lượt mua nhièu nhất thì lên dâu + match : hit -> điểm cao
    @Override
    public Page<SachDocument> searchTrendingBoost(Pageable pageable , String keyword){
        // cho phep sai 1
        Query queryMatch = Query.of(q->q.match(m->m.field("tenSach").query(keyword).fuzziness("1"))) ;

        // boost điểm số , ta sẽ kết hợp với lượt bán để đẩy lên trending tim kiếm
        // dùng logarit để giảm khoảng cách điểm số log(1+luot ban )
        FunctionScore functionScore = FunctionScore.of(f->f.fieldValueFactor(fvf->fvf.field("luotBan").modifier(FieldValueFactorModifier.Log1p).factor(1.2).missing(0.0))) ;

        // két hợp thành 1 query
        Query queryFinal = Query.of(q->q.functionScore(fsq->fsq.query(queryMatch).functions(functionScore).boostMode(FunctionBoostMode.Multiply))) ; // ưu tiên nhân

        // native
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(queryFinal).withPageable(pageable).build() ;

        // truy vấn lên
        SearchHits<SachDocument> searchHits = elasticsearchOperations.search(nativeQuery, SachDocument.class) ;

        // lấy sách document
        List<SachDocument> sachDocuments = searchHits.getSearchHits().stream().map(SearchHit::getContent).toList() ;

        return new PageImpl<>( sachDocuments, pageable ,searchHits.getTotalHits()) ;
    }

    // dùng zset mà làm thôi
    public void  searchKeyTrending(){}

    // hàm trả về danh sách các key word khi mới bấm vào thanh tìm kiếm (chưa gõ gì cả) - còn nếu gõ mk sẽ gọi hàm matchPhasePrefix bên trên
    public  List<String> getSearchKeyTrending(){return null ;}


    // chiến thuật băm nhỏ các từ khóa tìm kiếm ra ném thẳng vào cluster rồi lấy => cực mạnh

    // chiến thuật search rescore : boost Top n => tối ưu , giảm tải cho server Elastic
    // 2 vòng : vòng 1 match lấy top n , vòng 2 boost field
    // ***** hãy chú ý rằng nó có điển mù khi phân trang bằng Top N ****
    @Override
    public Page<SachDocument> searchRescore(Pageable pageable, String keyword) {
        // lấy tất car các doc có tên giống key lên
        Query queryMatch = Query.of(q -> q.match(m -> m.field("tenSach").query(keyword)));
        // bóost
        FunctionScore functionScore = FunctionScore.of(fs->fs
                .fieldValueFactor(fvf->fvf
                        .field("luotBan")
                        .modifier(FieldValueFactorModifier.Log1p)
                        .factor(1.2)
                        .missing(0.0))) ;
        //
        Query funcs = Query.of(q->q
                .functionScore(fs->fs
                        .query(Query.of(qq->qq.matchAll(ma->ma)))
                        .functions(functionScore)
                        .boostMode(FunctionBoostMode.Multiply)
                )
        ) ;

        NativeQuery nativeQueryRescore = NativeQuery.builder().withQuery(funcs).build() ;
        RescorerQuery rescoreQuery = new RescorerQuery(nativeQueryRescore)
                .withWindowSize(100)
                .withQueryWeight(1.0f)
                .withRescoreQueryWeight(1.0f) ;

        NativeQuery nativeQueryFinal =  NativeQuery.builder().withQuery(queryMatch).withPageable(pageable).build() ;
        nativeQueryFinal.addRescorerQuery(rescoreQuery);
        SearchHits<SachDocument> searchHits = elasticsearchOperations.search(nativeQueryFinal , SachDocument.class) ;
        List<SachDocument> sachDocuments = searchHits.getSearchHits().stream().map(SearchHit::getContent).toList() ;
        return new PageImpl<>(sachDocuments , pageable , searchHits.getTotalHits());
    }

    // sản phẩm tương tự - More like this (MLT) - gơij ý mua kèm khi bấm vào một sản phảm
    // min term freq : 1 từ dc coi là quan trong khi nón xuất hiẹn ở văn bản gốc tối thiểu bao nhieu lần
    // min doc freq : 1
    public List<SachDocument> moreLikeThis(int maSach) {
        Query boolQuery = Query.of(q->q.bool(b->b
                .must(m->m.moreLikeThis(mlt->mlt.fields("tenSach.text","moTa").like(l->l.document(d->d.index("sach").id(String.valueOf(maSach)))).maxQueryTerms(10).minDocFreq(1).minTermFreq(1)))
                        .filter(f->f.term(t->t.field("isActive").value(true)))
                 )
        ) ;
        // lấy ra 5 quyển gợi ý
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(boolQuery).withMaxResults(20).build() ;
        return elasticsearchOperations.search(nativeQuery, SachDocument.class).getSearchHits().stream().map(SearchHit::getContent).toList() ;

    }
    // hãy chú ý một điều rằng elastic sẽ gộp các fields mình chỉ định lại thành 1 đoạn văn bản rồi phân tích cÁC từu nó tách đc theo tần suát xuát hiên (min_term_freq) - tất nhiên sẽ loại bỏ các từ vô nghĩa : va, la , co , nghia ....
    // nếu đạt số lượng yêu caàu thì đem đi query xem có đạt số document match với từ khóa đấy không
    // cuối cùng nó query lấy lên nhữu document match vưới cái từu khóa đáy

}
