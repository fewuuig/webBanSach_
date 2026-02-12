package com.example.webbansach_backend.service;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Entity.RefreshToken;

public interface RefreshTokenService {
    public String createToken(NguoiDung nguoiDung) ;
    public RefreshToken verify(String token);
    public void delete(String token) ;
}
