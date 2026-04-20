package com.example.webbansach_backend.filter;

import com.example.webbansach_backend.service.UserService;
import com.example.webbansach_backend.service.impl.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService ;

    @Autowired
    private UserService userService ;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // cái này giúp khong cần đăng nhập lại sau mỗi request
        // sau khi xác minh xong nó sẽ lưu vào SecurtyContext
        // lấy heder từ client gửi về trong mỗi lần request , rồi tiến hnahf xác minh
        if(request.getRequestURI().startsWith("/wc")){
            filterChain.doFilter(request , response);
            return ;
        }
       try {
           String token = null ;
           String username = null ;
           // trả về header của request
           String authHeader = request.getHeader("Authorization") ;
           if(authHeader!=null && authHeader.startsWith("Bearer ")){
               token = authHeader.substring(7) ;
               username = jwtService.extractUsername(token);
           }
           if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
//               UserDetails userDetails = userService.loadUserByUsername(username) ; // lấy từ DB lên để check
               // tin tươngr jwwt vaf redis làm lơps lá chắn lần thứ 2
               if(jwtService.validateToken(token )){
                   List<String> roles = jwtService.extractRole(token) ;
                   if(roles == null){
                       filterChain.doFilter(request , response);
                   }
                   List<GrantedAuthority> grantedAuthorities =  roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                   UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username , null , grantedAuthorities) ;
                   authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                   SecurityContextHolder.getContext().setAuthentication(authenticationToken);
               }

           }
           filterChain.doFilter(request , response);
       }catch (ExpiredJwtException ex){
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           return ;
       }
    }
}
