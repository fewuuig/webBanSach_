package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.*;
import com.example.webbansach_backend.Repository.*;
import com.example.webbansach_backend.dto.book.AddBookRequestDTO;
import com.example.webbansach_backend.dto.book.BookResponeDTO;
import com.example.webbansach_backend.dto.book.BookUpdateDTO;
import com.example.webbansach_backend.exception.NotFoundException;
import com.example.webbansach_backend.mapper.BookMapper;
import com.example.webbansach_backend.service.BookService;
import com.example.webbansach_backend.utils.CheckRoleUItil;
import com.example.webbansach_backend.utils.ParseListUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private BookMapper bookMapper ;
    @Autowired
    private TheLoaiRepository theLoaiRepository ;
    @Autowired
    private SachTheLoaiRepository sachTheLoaiRepository ;
    @Autowired
    private HinhAnhRepository hinhAnhRepository ;
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    @Qualifier("paginate")
    private DefaultRedisScript<List> paginate ;
    @Autowired
    @Qualifier("addBook")
    private DefaultRedisScript<Long> addBook ;
    @Autowired
    @Qualifier("deleteBook")
    private DefaultRedisScript<Long> deleteBook ;
    @Override
    @Transactional
    public void addNewBook( String tenDangNhap,AddBookRequestDTO addBookRequestDTO){
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).
                orElseThrow(()->new RuntimeException("không đủ quyền để thực hiện tính năng này")) ;
        if(!CheckRoleUItil.checkRole(nguoiDung)) throw new RuntimeException("NGuoi dùng k đủ quyền để thêm sách") ;

        boolean exists = sachRepository.existsByIsbn(addBookRequestDTO.getIsbn()) ;
        if(exists) throw new RuntimeException("Sách đã tồn tại") ;

        // tạo thực thể
        Sach sachEntity = bookMapper.toEntity(addBookRequestDTO);
        sachEntity.setActive(true);
        // tìm thể loại
        TheLoai theLoai = theLoaiRepository.findByMaTheLoai(addBookRequestDTO.getMaTheLoai()).
                    orElseThrow(()-> new RuntimeException("Not find category:"+addBookRequestDTO.getMaTheLoai())) ;

        // tạo thể sach loại
        SachTheLoai sachTheLoai = new SachTheLoai() ;
        sachTheLoai.setSach(sachEntity);
        sachTheLoai.setTheLoai(theLoai);

        // tạo danh sách hình ảnh
        List<HinhAnh> hinhAnhs = new ArrayList<>();
        addBookRequestDTO.getHinhAnhDTOS().forEach(picture->{
            HinhAnh hinhAnh = new HinhAnh() ;
            hinhAnh.setDuLieuAnh(picture.getDuLieuAnh());
            hinhAnh.setTenHinhAnh(picture.getTenHinhAnh());
            hinhAnh.setSach(sachEntity);
            hinhAnhs.add(hinhAnh) ;
        });
        // save
        sachRepository.save(sachEntity) ;
        sachTheLoaiRepository.save(sachTheLoai) ;
        hinhAnhRepository.saveAll(hinhAnhs) ;
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        // them vào các key zset , value
                        redisTemplate.execute(addBook,
                                List.of("page_book_id" , "page_book_id_category:"+theLoai.getMaTheLoai(),"book:"+ sachEntity.getMaSach()),
                                sachEntity.getMaSach() ,sachEntity.getSoLuong()) ;
                        // => lua chỉ mất 1 round trip
                    }
                }
        );

    }

    // client tke theo kiểu chọn thể loại + tích checkbox id book=> gửi về 2id đó
    @Override
    @Transactional
    public void deleteBook(String tenDangNhap, List<Integer> ids , int maTheLoai) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow(()-> new RuntimeException("user k tồn tại")) ;
        if(!CheckRoleUItil.checkRole(nguoiDung)) throw new RuntimeException("NGuoi dùng k đủ quyền để thêm sách") ;
        sachRepository.updateIsActive(ids);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                List<String> idString = ids.stream().map(String::valueOf).toList();
                System.out.println(idString);
                // xóa key page_book_id
                ids.forEach(id->{
                    redisTemplate.opsForZSet().remove("page_book_id" , id) ;
                    // xáo key "page_book_id_category:" + maTheLoai
                    redisTemplate.opsForZSet().remove("page_book_id_category:" + maTheLoai , maTheLoai ,id) ;
                });
                // xóa book_info:
                List<String> idBookInfo = ids.stream().map(id->"book_info:"+id).toList() ;
                redisTemplate.delete(idBookInfo) ;


//                List<String> keys = List.of(
//                        "page_book_id",
//                        "page_book_id_category:" + maTheLoai,
//                        "book_info:"
//                );
//
//                System.out.println("==== DEBUG REDIS LUA ====");
//                System.out.println("KEYS: " + keys);
//                System.out.println("ARGV: " + idString);
//                System.out.println("idString: " +  idString.toArray(new String[0]));
//                Long result = redisTemplate.execute(
//                        deleteBook,
//                        keys,
//                        idString.toArray()
//                );
//                System.out.println("đã xóa : "+result);
            }
        });
    }
    @Override
    @Transactional
    public void updateBook(String tenDangNhap , BookUpdateDTO bookUpdateDTO){
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow(()-> new RuntimeException("user k tồn tại")) ;
        if(!CheckRoleUItil.checkRole(nguoiDung)) throw new RuntimeException("người dùng k đủ quyền để cập nhật sách") ;

        Sach sach = sachRepository.findByMaSachAndIsActive(bookUpdateDTO.getMaSach(),true).orElseThrow() ;
         bookMapper.updateFromDTO(bookUpdateDTO ,sach); ;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // cap nhatr chi can xóa key
                System.out.println("updated:book:"+bookUpdateDTO.getMaSach());
                redisTemplate.delete("book_info:"+bookUpdateDTO.getMaSach()) ;
            }
        });

    }
    @Override
    @Transactional
    public List<BookResponeDTO> getBookCategory(int maTheLoai){
        // tận dụng lại key phân trang . lấy tất cả lên
        List<Object> ids = redisTemplate.execute(paginate , List.of("page_book_id_category:"+maTheLoai),0 , -1) ;

        List<Integer> idBooks = ParseListUtil.toListNumber(ids) ;

        List<String> keyInfo = ParseListUtil.toKeyBookInfo( idBooks ,"book_info:") ;

        List<Object> cached = redisTemplate.opsForValue().multiGet(keyInfo);
        List<Integer> idNotFind = new ArrayList<>() ;
        List<BookResponeDTO> bookResponeDTOS = new ArrayList<>() ;
        int index = 0;
        for(Object obj : cached){
            if(obj != null ){
                System.out.println("lấy từ cache");
                bookResponeDTOS.add((BookResponeDTO) obj) ;
            }else {
                idNotFind.add(idBooks.get(index)) ;
            }
            index ++ ;
        }
        if(!idNotFind.isEmpty()){
            List<Sach> saches = sachRepository.findByMaSachInAndIsActiveAndMaTheLoai(idNotFind , maTheLoai) ;

            for(Sach sach : saches){
                System.out.println("lookup:"+sach.getMaSach());
                bookResponeDTOS.add(bookMapper.toDTO(sach)) ;
                redisTemplate.opsForValue().set("book_info:"+sach.getMaSach() , bookMapper.toDTO(sach) , 1 ,TimeUnit.HOURS);
            }
        }

        bookResponeDTOS.sort(Comparator.comparing(BookResponeDTO::getTenSach));
        return bookResponeDTOS ;
    }


    public List<BookResponeDTO> getSachNew(){
        List<Sach> saches=sachRepository.findSachNew() ;
        if(saches == null) return null ;
        else return saches.stream().map(bookMapper::toDTO).toList() ;
    }
}
