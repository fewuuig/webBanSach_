package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Entity.RefreshToken;
import com.example.webbansach_backend.Repository.RefreshTokenRepository;
import com.example.webbansach_backend.service.RefreshTokenService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository ;

    @Override
    public String createToken(NguoiDung nguoiDung){
        RefreshToken rt = new RefreshToken() ;
        rt.setNguoiDung(nguoiDung);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiryData(new Date(System.currentTimeMillis() +  7*24*60*60*1000));
        // khi lưu nguoiDung vào Refreshtoken thì khi chạy hybernate nó chỉ lấy nguoiDung.id để nó làm khóa ngaoij
        // => đó là cơ chế ngầm
        refreshTokenRepository.save(rt) ;
        return rt.getToken() ;
    }
    @Override
    public RefreshToken verify(String token){
        RefreshToken rt = refreshTokenRepository.findByToken(token).orElseThrow(()->new EntityNotFoundException("không thấy token ở DB")) ;

        if(rt.getExpiryData().before(new Date())){
            refreshTokenRepository.deleteByToken(token);
            throw new RuntimeException("RefreshToken đã hết hạn") ;
        }
        return rt ;
    }
    @Override
    public void delete(String token){
            refreshTokenRepository.deleteByToken(token);
    }

}
