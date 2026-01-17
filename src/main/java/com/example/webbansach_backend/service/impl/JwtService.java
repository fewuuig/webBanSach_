package com.example.webbansach_backend.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    // khóa bí mật
    private static final String SECRET_KEY = "8492384NNK34234234238084M234234KK23423MH4234LF4234" ;

    // taọ token để lưu trên local storage or cookie
    public String generateToken(String username){
        // tại nội dung payload của token
        Map<String , Object> claims = new HashMap<>() ;

        return createToken(claims , username ) ;
    }
    private String createToken(Map<String , Object> claims , String username ){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 100000L * 60 * 60 * 1000) )
                .signWith(SignatureAlgorithm.HS256 , getSignKey())
                .compact() ;
    }

    // chuyển chữ ký sang dạng byte
    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes) ;
    }

    // trích xuất toàn bộ claims : đầu tiên đọc , xác thực token , so sánh chữ ký xem có giống sever không  , phân tích token -> header , payload(claims) , signature . rồi lấy phần thân Object paylaod(claims)
    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(getSignKey()).parseClaimsJws(token).getBody() ;
    }
    // trích xuất thông tin cho 1 claim (mỗi 1 key : value đc gọi là 1 claim . nhièu nó bên trong thằng Map thì được gọi là : claims  )
    // chỗn ày giúp sủ lý ngắn gọn viẹc lấy ra claim mà nguiowuf dùng muốn lấy để xác thực điều gì đó
    public <T> T extractClaim(String token , Function<Claims , T> claimsTFunction){
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims) ;
    }

    // lấy ra thời hnaj đăng nhập
    public Date extractExpiration(String token){
        return extractClaim(token , Claims::getExpiration) ;
    }

    // lấy ra ten đăg nhập
    public String extractUsername(String token){
        return extractClaim(token , Claims::getSubject) ;
    }

    // kiểm tra token đã hết hạn chưa
    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date()) ;
    }

    // kiêmr tra tínhd hợp lệ đăng nhập
    public Boolean validateToken(String token , UserDetails userDetails){
        String username = extractUsername(token) ;
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)) ;
    }


}
