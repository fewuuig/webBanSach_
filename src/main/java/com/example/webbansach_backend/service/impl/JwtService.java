package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Entity.Quyen;
import com.example.webbansach_backend.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;

    // Khóa bí mật
    private static final String SECRET_KEY = "8492384NNK34234234238084M234234KK23423MH4234LF4234";
    private Key cachedKey;
    private JwtParser jwtParser;
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        this.cachedKey = Keys.hmacShaKeyFor(keyBytes);

        // Cache luôn cái máy giải mã.
        this.jwtParser = Jwts.parser().setSigningKey(this.cachedKey);
    }

    // Taọ token để lưu trên local storage or cookie
    public String generateToken(String username){
        Map<String , Object> claims = new HashMap<>();
        claims.put("role" , true);

        NguoiDung nguoiDung = userService.findByUsername(username);
        List<Quyen> quyen = new ArrayList<>();
        nguoiDung.getNguoiDungQuyens().forEach(ndq -> {
            quyen.add(ndq.getQuyen());
        });

        boolean isAdmin = false;
        boolean isStaff = false;
        boolean isUser = false;

        for(Quyen q : quyen){
            if(q.getTenQuyen().equals("ADMIN")) isAdmin = true;
            if(q.getTenQuyen().equals("STAFF")) isStaff = true;
            if(q.getTenQuyen().equals("USER")) isUser = true;
        }
        claims.put("isAdmin" , isAdmin);
        claims.put("isStaff" , isStaff);
        claims.put("isUser" , isUser);

        return createToken(claims , username);
    }

    private String createToken(Map<String , Object> claims , String username ){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Đã fix bug toán học: 7 ngày * 24h * 60m * 60s * 1000ms. Thêm chữ 'L' để tránh tràn bộ nhớ kiểu int.
                .setExpiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000) )
                .signWith(SignatureAlgorithm.HS256 , this.cachedKey) // Dùng key đã cache
                .compact();
    }

    // Trích xuất toàn bộ claims (Siêu nhanh nhờ dùng lại jwtParser)
    private Claims extractAllClaims(String token){
        return this.jwtParser.parseClaimsJws(token).getBody();
    }

    // Trích xuất thông tin cho 1 claim
    public <T> T extractClaim(String token , Function<Claims , T> claimsTFunction){
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    // Lấy ra thời hạn đăng nhập
    public Date extractExpiration(String token){
        return extractClaim(token , Claims::getExpiration);
    }

    // Lấy ra Roles (Trực tiếp từ Token, không cần gọi DB)
    public List<String> extractRole(String token){
        Claims claims = extractAllClaims(token);
        List<String> roles = new ArrayList<>();
        if(claims.get("isAdmin" , Boolean.class)) roles.add("ADMIN");
        if(claims.get("isStaff" , Boolean.class)) roles.add("STAFF");
        if(claims.get("isUser" , Boolean.class)) roles.add("USER");
        return roles;
    }

    // Lấy ra tên đăng nhập
    public String extractUsername(String token){
        return extractClaim(token , Claims::getSubject);
    }

    // Kiểm tra token đã hết hạn chưa
    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    // Kiểm tra tính hợp lệ đăng nhập (Nhanh như điện vì chỉ check Redis)
    public Boolean validateToken(String token ){
        try {
            // Lưu ý: thư viện jjwt sẽ tự động throw ExpiredJwtException khi gọi extractAllClaims
            // nếu token hết hạn. Nên khối try-catch này bắt lỗi rất an toàn.
            if(isTokenExpired(token)) return false;
            String username = extractUsername(token);
            return (username.equals(redisTemplate.opsForValue().get("username:" + username)));
        }catch (Exception ex){
            return false;
        }
    }
}