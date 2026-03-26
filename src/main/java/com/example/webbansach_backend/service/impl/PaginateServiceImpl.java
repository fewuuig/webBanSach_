package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.Repository.SachRepository;
import com.example.webbansach_backend.dto.book.BookResponeDTO;
import com.example.webbansach_backend.mapper.BookMapper;
import com.example.webbansach_backend.service.PaginateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private List<Integer> convertIdToNumber(List<Object> ids){
        return ids.stream().map(id->Integer.parseInt(id.toString())).toList() ;
    }

    private List<String> convertKeyBookInfo(List<Integer> ids , String keyInfo){
        return ids.stream().map(id ->keyInfo+id).toList() ;
    }

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
        List<Integer> bookIdList = convertIdToNumber(bookIds) ;

        List<Integer> idNotFind = new ArrayList<>() ; // id sách không tìm thấy

        List<String> keyInfo = convertKeyBookInfo(bookIdList , keyBookInfo)  ; // key info book

        List<Object> cacheBooks = redisTemplate.opsForValue().multiGet(keyInfo) ;  // lâys list info book
        int index = 0 ;
        // duy trì thứu tự của các quyển sách khi phân trang
        Map<Integer , BookResponeDTO> order = new TreeMap<>() ;
        for(Object bookDTO : cacheBooks){
            if(bookDTO != null ){
                order.put(bookIdList.get(index) ,(BookResponeDTO) bookDTO) ;
            }else {
                idNotFind.add(bookIdList.get(index)) ; // chuyển sang string
            }
            index ++ ;
        }

        if(!idNotFind.isEmpty()){
            System.out.println("lookup DB ");
            List<Sach> saches = sachRepository.findByMaSachIn(idNotFind) ;

            for(Sach sach : saches){
                order.put(sach.getMaSach() ,bookMapper.toDTO(sach) ) ;
                redisTemplate.opsForValue().set(keyBookInfo+ sach.getMaSach() , bookMapper.toDTO(sach) , 1 ,TimeUnit.HOURS);
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
        Sach sach = sachRepository.findByMaSach(maSach).orElse(null) ;

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

}
