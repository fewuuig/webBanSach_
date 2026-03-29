package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.Repository.SachRepository;
import com.example.webbansach_backend.Repository.customer.SachCustomRepository;
import com.example.webbansach_backend.builder.BookSearchBuiler;
import com.example.webbansach_backend.converter.book.BookSearchBuilderConverter;
import com.example.webbansach_backend.dto.book.BookResponeDTO;
import com.example.webbansach_backend.mapper.BookMapper;
import com.example.webbansach_backend.service.PaginateService;
import com.example.webbansach_backend.utils.ParseListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.JpaEntityGraph;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
// chống dame băngd rate limit
@Service
public class PaginateServiceImpl implements PaginateService {
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private BookMapper bookMapper ;
    @Autowired
    @Qualifier("paginate")
    private DefaultRedisScript<List> paginate ;
    @Autowired
    private SachCustomRepository sachCustomRepository ;
    @Autowired
    @Qualifier("priceFilter")
    private DefaultRedisScript<List> priceFilter ;
    private Page<BookResponeDTO> getBookPage(int maTheLoai , int page , int size , String zKey , String keyBookInfo){
        // bắt đâyuf trang
        int start = page*size ;
        // kết thúc trang
        int end = start + size - 1 ;
        // lấy id sách cần cho việc phân trang
        List<Object> bookIds = new ArrayList<>() ;
        if(maTheLoai == -1) {
            bookIds = redisTemplate.execute(paginate, Arrays.asList(zKey), start, end);
        }
        else bookIds = redisTemplate.execute(paginate , Arrays.asList(zKey+maTheLoai) ,start ,end) ;
        // chuyển sang list int
        List<Integer> bookIdList = ParseListUtil.toListNumber(bookIds) ;

        List<Integer> idNotFind = new ArrayList<>() ; // id sách không tìm thấy

        List<String> keyInfo = ParseListUtil.toKeyBookInfo(bookIdList , keyBookInfo)  ; // key info book

        List<Object> cacheBooks = redisTemplate.opsForValue().multiGet(keyInfo) ;  // lâys list info book
        int index = 0 ;
        // duy trì thứ tự của các quyển sách khi phân trang
        Map<Integer , BookResponeDTO> order = new TreeMap<>() ;
        for(Object bookDTO : cacheBooks){
            if(bookDTO != null ){
                order.put(bookIdList.get(index) ,(BookResponeDTO) bookDTO) ;
            }else {
                idNotFind.add(bookIdList.get(index)) ;
            }
            index ++ ;
        }

        if(!idNotFind.isEmpty()){
            System.out.println("lookup DB ");
            List<Sach> saches = sachRepository.findByMaSachInAndIsActive(idNotFind,true) ;

            for(Sach sach : saches){
                order.put(sach.getMaSach() ,bookMapper.toDTO(sach) ) ;
                redisTemplate.opsForValue().set(keyBookInfo + sach.getMaSach() , bookMapper.toDTO(sach) , 1 ,TimeUnit.HOURS);
            }

        }else System.out.println("redis cache đã có dưx liệu");
        // chuyển Map sang list
        List<BookResponeDTO> bookResponeDTOS =  new ArrayList<>(order.values()) ;
        Collections.reverse(bookResponeDTOS);

        long totalElement = 0 ;
        if (maTheLoai == -1)  totalElement = redisTemplate.opsForZSet().size(zKey) ;
        else totalElement = redisTemplate.opsForZSet().size(zKey+maTheLoai) ;

        return new PageImpl<>(bookResponeDTOS , PageRequest.of(page ,size) , totalElement) ;

    }

    @Override
    public Page<BookResponeDTO> getBookPageAndSize( int page ,int size ){
       return getBookPage(-1 , page , size ,"page_book_id" , "book_info:") ;

    }

    // lấy sachs lên theo thể loại
    public Page<BookResponeDTO> getBookCategoryAndPageAndSize(int maTheLoai , int page , int size){
        // phân trang
        return getBookPage(maTheLoai , page , size ,"page_book_id_category:" , "book_info:" ) ;
    }


    // ở đay có thể làm hot key redis cluster
    @Override
    public BookResponeDTO getInfoBook(int maSach){
        String key = "book_info:" +maSach ;
        // check cache truowcs
        Object cached = redisTemplate.opsForValue().get(key) ;


        if (cached!= null){
            try {
                cached = (BookResponeDTO) cached ;
            }catch(Exception ex){
                System.out.println("trả về null");
                return null ;
            }
            System.out.println("lấy từ cache");
            return  (BookResponeDTO) cached ;
        }
        System.out.println("tìm kiếm lại");

        // nếu không có trong cache thì tìm DB
        Sach sach = sachRepository.findByMaSachAndIsActive(maSach ,true).orElse(null) ;

        if(sach == null ){
            // cho nó luư 5 phút sau check lại nếu có người call API
            redisTemplate.opsForValue().set(key , "NULL" , 5 , TimeUnit.MINUTES);
            return null ;
        }else {
            // lưu tạm 1 tiêng
            BookResponeDTO bookResponeDTO = bookMapper.toDTO(sach) ;
            redisTemplate.opsForValue().set(key , bookResponeDTO , 1 , TimeUnit.HOURS);
            return bookResponeDTO ;
        }
    }
    public Page<BookResponeDTO> searchFilter(Map<String , Object> params, Pageable pageable){
        BookSearchBuiler bookSearchBuiler = BookSearchBuilderConverter.toBookSearchBuiler(params) ;
        Page<BookResponeDTO> page ;
        page = checkCategory(bookSearchBuiler , pageable) ;
        if(page != null) {
            System.out.println("có vào filter category ");
            return page;
        }
        page = checkPriceFilter(bookSearchBuiler,pageable) ;
        if(page != null ) {
            System.out.println("có vào filter price ");
            return page;
        }
        page = checkCategoryAndPrice(bookSearchBuiler , pageable) ;
        if(page != null) {
            System.out.println("có vào filter cate and price ");
            return page ;
        }

        return sachCustomRepository.findBookFilter(bookSearchBuiler , pageable) ;
    }
    private Page<BookResponeDTO> checkCategory(BookSearchBuiler bookSearchBuiler , Pageable pageable){
        if(bookSearchBuiler.getma_the_loai() != null
                && bookSearchBuiler.getten_tac_gia()==null
                && bookSearchBuiler.getPriceTo()==null
                && bookSearchBuiler.getPriceFrom()==null){
            int maTheLoai = bookSearchBuiler.getma_the_loai() ;
            System.out.println("vào đây");
            Page<BookResponeDTO>  page = getBookPage(maTheLoai ,
                    pageable.getPageNumber(),
                    (int)pageable.getPageSize() ,
                    "page_book_id_category:" ,"book_info:") ;
            System.out.println("vào đây2");
            if(!page.getContent().isEmpty()){
                return new PageImpl<>(page.getContent() ,pageable , page.getContent().size()) ;
            }
        }
        return null ;
    }
    private Page<BookResponeDTO> checkPriceFilter(BookSearchBuiler bookSearchBuiler , Pageable pageable){
        //check ram : priceFrom , priceTo
        if(bookSearchBuiler.getma_the_loai() == null
                && bookSearchBuiler.getten_tac_gia()==null
                && (bookSearchBuiler.getPriceTo()!=null
                || bookSearchBuiler.getPriceFrom()!=null)){

            List<Object> ids = null ;
            Double priceFrom = bookSearchBuiler.getPriceFrom() ;
            Double priceTo = bookSearchBuiler.getPriceTo() ;
            if(priceFrom != null && priceTo != null){
                ids = redisTemplate.execute(priceFilter,List.of("price") , priceFrom ,priceTo , pageable.getOffset() , pageable.getPageSize() ) ;
            }
            if(priceFrom == null && priceTo != null){
                ids = redisTemplate.execute(priceFilter,List.of("price") , 0 ,priceTo ,pageable.getOffset() , pageable.getPageSize() ) ;
            }
            if(priceFrom != null && priceTo == null){
                ids = redisTemplate.execute(priceFilter,List.of("price") , priceFrom , Double.MAX_VALUE,pageable.getOffset() , pageable.getPageSize()) ;
            }

            // chuyền ids về int
            if(!ids.isEmpty() ) {
                List<Integer> idNumber = ParseListUtil.toListNumber(ids);
                List<String> keyBookInfo = ParseListUtil.toKeyBookInfo(idNumber ,"book_info:") ;
                List<Object> cached = redisTemplate.opsForValue().multiGet(keyBookInfo) ;
                List<Integer> idNotFind = new ArrayList<>() ; // id sách không tìm thấy
                int index = 0 ;
                // duy trì thứ tự của các quyển sách khi phân trang
                Map<Integer , BookResponeDTO> order = new TreeMap<>() ;
                for(Object bookDTO : cached){
                    if(bookDTO != null ){
                        order.put(idNumber.get(index) ,(BookResponeDTO) bookDTO) ;
                    }else {
                        idNotFind.add(idNumber.get(index)) ;
                    }
                    index ++ ;
                }

                if(!idNotFind.isEmpty()){
                    System.out.println("lookup DB ");
                    List<Sach> saches = sachRepository.findByMaSachInAndIsActive(idNotFind,true) ;

                    for(Sach sach : saches){
                        order.put(sach.getMaSach() ,bookMapper.toDTO(sach) ) ;
                        System.out.println("cache lại vào ram");
                        redisTemplate.opsForValue().set("book_info:" + sach.getMaSach() , bookMapper.toDTO(sach) , 1 ,TimeUnit.HOURS);
                    }

                }else System.out.println("redis cache đã có dưx liệu");
                // chuyển Map sang list
                List<BookResponeDTO> bookResponeDTOS =  new ArrayList<>(order.values()) ;
                Collections.reverse(bookResponeDTOS);

                long totalElement =redisTemplate.opsForZSet().count("price" , priceFrom!=null?priceFrom:0 , priceTo!=null?priceTo:Double.MAX_VALUE) ;

                return new PageImpl<>(bookResponeDTOS , pageable , totalElement) ;

            }
        }
        return null ;
    }

    private Page<BookResponeDTO> checkCategoryAndPrice(BookSearchBuiler bookSearchBuiler , Pageable pageable){
        if(bookSearchBuiler.getma_the_loai() != null
                && bookSearchBuiler.getten_tac_gia()==null
                && (bookSearchBuiler.getPriceTo()!=null
                || bookSearchBuiler.getPriceFrom()!=null)){
            int start = pageable.getPageNumber() * pageable.getPageSize() ;
            int end = start + pageable.getPageSize() -1 ;
            int maTheLoai = bookSearchBuiler.getma_the_loai() ;
            List<Object> idCategory = redisTemplate.execute(paginate ,List.of("page_book_id_category:"+maTheLoai),start ,end ) ;
            List<Object> idPrice = null ;
            Double priceFrom = bookSearchBuiler.getPriceFrom() ;
            Double priceTo = bookSearchBuiler.getPriceTo() ;
            if(priceFrom != null && priceTo != null){
                idPrice = redisTemplate.execute(priceFilter,List.of("price") , priceFrom ,priceTo ) ;
            }
            if(priceFrom == null && priceTo != null){
                idPrice = redisTemplate.execute(priceFilter,List.of("price") , 0 ,priceTo ) ;
            }
            if(priceFrom != null && priceTo == null){
                idPrice = redisTemplate.execute(priceFilter,List.of("price") , priceFrom , Double.MAX_VALUE) ;
            }

            List<Integer> idCategoryNumber = ParseListUtil.toListNumber(idCategory) ;
            List<Integer> idPriceNumber = ParseListUtil.toListNumber(idPrice) ;
            System.out.println("id category : "+idCategoryNumber);
            System.out.println("id priceNumber : " + idPriceNumber);
            // lấy giao của nó => đc sách chung
            Set<Integer> idCate = new HashSet<>(idCategoryNumber) ;
            List<Integer>idBook =  idPriceNumber.stream().filter(idCate::contains).toList() ;
            System.out.println("id book giao : " + idBook);
            List<String> keyInfo = ParseListUtil.toKeyBookInfo(idBook ,"book_info:") ;
            List<Object> cached = redisTemplate.opsForValue().multiGet(keyInfo) ;

            List<Integer> idNotFind = new ArrayList<>() ;
            int index = 0 ;
            Map<Integer , BookResponeDTO> order = new TreeMap<>() ;
            for(Object bookDTO : cached){
                if(bookDTO != null ){
                    order.put(idBook.get(index) ,(BookResponeDTO) bookDTO) ;
                }else {
                    idNotFind.add(idBook.get(index)) ;
                }
                index ++ ;
            }

            if(!idNotFind.isEmpty()){
                System.out.println("lookup DB ");
                List<Sach> saches = sachRepository.findByMaSachInAndIsActive(idNotFind,true) ;

                for(Sach sach : saches){
                    order.put(sach.getMaSach() ,bookMapper.toDTO(sach) ) ;
                    redisTemplate.opsForValue().set("book_info:" + sach.getMaSach() , bookMapper.toDTO(sach) , 1 ,TimeUnit.HOURS);
                }

            }else System.out.println("redis cache đã có dưx liệu");
            // chuyển Map sang list
            List<BookResponeDTO> bookResponeDTOS =  new ArrayList<>(order.values()) ;
            Collections.reverse(bookResponeDTOS);


            return new PageImpl<>(bookResponeDTOS, pageable, idBook.size());
        }
        return null ;
    }

}
