package com.example.webbansach_backend.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JwtRespone {
    private final String accessToken ;
    private final String refreshToken ;



}
