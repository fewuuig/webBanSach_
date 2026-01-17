//package com.example.webbansach_backend.filter;
//
//import com.example.webbansach_backend.service.UserService;
//import com.example.webbansach_backend.service.impl.JwtService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//@Component
//public class AuthenticationFilter extends OncePerRequestFilter {
//    @Autowired
//    private JwtService jwtService ;
//
//    @Autowired
//    private UserService userService ;
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        // cái này giúp khong cần đăng nhập lại sau mỗi request
//        // sau khi xác minh xong nó sẽ lưu vào SecurtyContext
//        // lấy heder từ client gửi về trong mỗi lần request , rồi tiến hnahf xác minh
//        String token = null ;
//        String username = null ;
//        // trả về header của request
//        String authHeader = request.getHeader("Authorization") ;
//        if(authHeader!=null && authHeader.startsWith("Bearer ")){
//            token = authHeader.substring(7) ;
//            username = jwtService.extractUsername(token);
//        }
//        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
//            UserDetails userDetails = userService.loadUserByUsername(username) ;
//            if(userDetails !=null && jwtService.validateToken(token , userDetails) ){
//                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails , null , userDetails.getAuthorities()) ;
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            }
//            filterChain.doFilter(request , response);
//        }
//    }
//}
