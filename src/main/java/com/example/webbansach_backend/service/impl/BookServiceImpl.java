package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.HinhAnh;
import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.Entity.SachTheLoai;
import com.example.webbansach_backend.Entity.TheLoai;
import com.example.webbansach_backend.Repository.HinhAnhRepository;
import com.example.webbansach_backend.Repository.SachRepository;
import com.example.webbansach_backend.Repository.SachTheLoaiRepository;
import com.example.webbansach_backend.Repository.TheLoaiRepository;
import com.example.webbansach_backend.dto.book.AddBookRequestDTO;
import com.example.webbansach_backend.mapper.BookMapper;
import com.example.webbansach_backend.service.BookService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

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
    @Transactional
    public void addNewBook(AddBookRequestDTO addBookRequestDTO){
        boolean exists = sachRepository.existsByIsbn(addBookRequestDTO.getIsbn()) ;
        if(exists) throw new RuntimeException("Sách đã tồn tại") ;

        // tạo thực thể
        Sach sachEntity = bookMapper.toEntity(addBookRequestDTO);
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
                        redisTemplate.opsForZSet().add("page_book_id" , sachEntity.getMaSach() , sachEntity.getMaSach()) ;
                    }
                }
        );

    }
}
