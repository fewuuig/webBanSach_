package com.example.webbansach_backend.Repository;

import com.example.webbansach_backend.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken , Integer> {
    Optional<RefreshToken> findByToken(String token) ;
    void  deleteByToken(String token) ;
}
