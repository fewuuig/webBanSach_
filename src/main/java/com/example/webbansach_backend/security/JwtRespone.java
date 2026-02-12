package com.example.webbansach_backend.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
public class JwtRespone {
    private final String accessToken ;
    private final String refreshToken ;



}
