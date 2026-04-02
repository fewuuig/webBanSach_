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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
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
    @Autowired
    @Qualifier("carousel")
    private DefaultRedisScript<List> carousel ;
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
                        redisTemplate.execute(addBook,
                                List.of("page_book_id" , "page_book_id_category:"+theLoai.getMaTheLoai(),"book:"+ sachEntity.getMaSach()),
                                sachEntity.getMaSach() ,sachEntity.getSoLuong()) ;
                    }
                }
        );

    }

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
                    // xóa khỏi image:book:
                    redisTemplate.delete("image:book:"+id) ;
                    //xóa khỏi zset price
                    redisTemplate.opsForZSet().remove("price" , id) ;
                    // xóa khỏi top:selling
                    redisTemplate.opsForZSet().remove("top:selling:books" ,id ) ;
                    redisTemplate.opsForZSet().remove("page_book_id" , id) ;
                    // xáo key "page_book_id_category:" + maTheLoai
                    redisTemplate.opsForZSet().remove("page_book_id_category:" + maTheLoai , maTheLoai ,id) ;
                });
                // xóa book_info:
                List<String> idBookInfo = ids.stream().map(id->"book_info:"+id).toList() ;

                redisTemplate.delete(idBookInfo) ;
            }
        });
    }
    @Override
    @Transactional
    public void updateBook(String tenDangNhap , BookUpdateDTO bookUpdateDTO){
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).
                orElseThrow(()-> new RuntimeException("user k tồn tại")) ;
        if(!CheckRoleUItil.checkRole(nguoiDung)) throw new RuntimeException("người dùng k đủ quyền để cập nhật sách") ;

        Sach sach = sachRepository.findByMaSachAndIsActiveFetchImg(bookUpdateDTO.getMaSach(),true).orElseThrow() ;
         bookMapper.updateFromDTO(bookUpdateDTO ,sach);

         // thêm ảnh
        List<HinhAnh> hinhAnhs = new ArrayList<>() ;
        if(bookUpdateDTO.getHinhAnhDTOS() != null){
            bookUpdateDTO.getHinhAnhDTOS().forEach(hinhAnh->{
                HinhAnh entity = new HinhAnh() ;
                entity.setSach(sach);
                entity.setTenHinhAnh(hinhAnh.getTenHinhAnh());
                entity.setDuLieuAnh(hinhAnh.getDuLieuAnh());

                hinhAnhs.add(entity) ;
            });
            sach.getDanhSachHinhAnh().addAll(hinhAnhs) ;
        }
        // xóa ảnh nếu có
        if(bookUpdateDTO.getIdImgDelete()!=null){
            sach.getDanhSachHinhAnh().removeIf(img ->bookUpdateDTO.getIdImgDelete().contains(img.getMaHinhAnh())) ;
        }
        sachRepository.save(sach) ;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                redisTemplate.delete("book_info:"+bookUpdateDTO.getMaSach());
                if(bookUpdateDTO.getIdImgDelete()!=null || bookUpdateDTO.getHinhAnhDTOS() != null ){
                    redisTemplate.delete("image:book:"+bookUpdateDTO.getMaSach()) ;
                }
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
    @Override
    public List<BookResponeDTO>  bestSellerCarousel(){
        // lấy trong zset key : top:selling:books . lấy ra 3 quyển
        List<Object> bookIds = redisTemplate.execute(carousel , List.of("top:selling:books") ,0 ,2) ;
        if( bookIds.isEmpty()||bookIds == null){
            System.out.println("vào đây1");
            List<Object> ids = redisTemplate.execute(paginate ,List.of("page_book_id") , 0 , 2) ;
            return convertBookRespDTOCache(ids) ;
        }
        // nếu k đủ 3 thì lấy 2 .. 1 cái đầu của page id
        if(bookIds.size() !=3){
            List<Object> ids = redisTemplate.execute(paginate ,List.of("page_book_id") ,  0, bookIds.size()==1?1:0) ;
            // gộp id của bookidsd vào đay
            bookIds.forEach(id->{
                assert ids != null;
                ids.add(id) ;
            });
            Set<Object> idFinal = new TreeSet<>(ids) ;
            System.out.println("size set : " + idFinal.size());
            if(idFinal.size()==2){
                for(Object obj : idFinal){
                    int tmp = (int)obj - 1 ;
                    idFinal.add(tmp) ;
                    break ;
                }

            }else if(idFinal.size() == 1){
                for(Object obj : idFinal){
                    int tmp = (int)obj - 1 ;
                    idFinal.add(tmp) ;
                    break ;
                }
                for(Object obj : idFinal){
                    int tmp = (int)obj - 1 ;
                    idFinal.add(tmp) ;
                    break ;
                }
            }
            ids.clear();
            ids.addAll(idFinal) ;
            return convertBookRespDTOCache(ids) ;
        }
        System.out.println("vào đây 3");
        return convertBookRespDTOCache(bookIds) ;
    }
    @Override
    public List<BookResponeDTO> getSachNew(){
        List<Sach> saches=sachRepository.findSachNew() ;
        if(saches == null) return null ;
        else return saches.stream().map(bookMapper::toDTO).toList() ;
    }
    @Override
    public List<BookResponeDTO> getBookDeleted(int maTheLoai){
        List<Sach>saches = sachRepository.findSachDeleted(maTheLoai) ;
        return saches.stream().map(bookMapper::toDTO).toList() ;
    }
    @Override
    @Transactional
    public void reStoreBook(List<Integer> ids , int maTheLoai ){
        sachRepository.reStoreBook(ids);
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        // thêm id vào category , page
                        ids.forEach(id->{
                            redisTemplate.opsForZSet().add("page_book_id" , id ,id) ;
                            redisTemplate.opsForZSet().add("page_book_id_category:"+maTheLoai,id ,id ) ;
                        });
                    }
                }
        );
    }
    public List<BookResponeDTO> convertBookRespDTOCache(List<Object> bookIds){
        List<Integer> bookIdNumber = ParseListUtil.toListNumber(bookIds) ;
        List<String> keyBookInfo = ParseListUtil.toKeyBookInfo(bookIdNumber ,"top:selling:books") ;
        List<Object> caches = redisTemplate.opsForValue().multiGet(keyBookInfo);

        List<BookResponeDTO> bookResponeDTOS = new ArrayList<>() ;
        List<Integer> idNotFind = new ArrayList<>() ;
        int index = 0 ;
        for(Object cache : caches){
            if(cache!= null){
                bookResponeDTOS.add((BookResponeDTO) cache) ;
            }else idNotFind.add(bookIdNumber.get(index)) ;
            index ++ ;
        }
        if(!idNotFind.isEmpty()){
            List<Sach> saches = sachRepository.findByMaSachInAndIsActive( idNotFind,true) ;
            saches.forEach(sach->{
                bookResponeDTOS.add(bookMapper.toDTO(sach)) ;
            });
        }
        return bookResponeDTOS ;
    }


}
