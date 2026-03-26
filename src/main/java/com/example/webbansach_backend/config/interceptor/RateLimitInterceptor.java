//package com.example.webbansach_backend.config.interceptor;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class RateLimitInterceptor implements HandlerInterceptor {
//    @Autowired
//    private RedisTemplate<String , Object> redisTemplate ;
//    private static final int LIMIT = 30 ; // chỉ nhận 25 request /30s
//    private static final int TIMELIMT = 10 ; // 30s nhận 25 requets
//
//    // mỗi người dùng chỉ cho 30 request thôi / hoặc guest cx thế / hoặc là ghan theo url để tabwg trải nghiêmj nghười dùng cx đc
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication() ;
//        String url = request.getRequestURI();
//        String ip = request.getRemoteAddr() ;
//        String key ="" ;
//        if(url != null && !authentication.getPrincipal().equals("anonymousUser")){
//            key = "rate_limit:"+url+":"+authentication.getName() ;
//        }else if(url != null && authentication.getPrincipal().equals("anonymousUser")){
//            key = "rate_limit:"+url +":"+ip ;
//        }
//        long count = redisTemplate.opsForValue().increment(key) ;
//        if(count == 1) redisTemplate.expire(key , 30000 , TimeUnit.MILLISECONDS);
//
//        if(count >39){
//            response.setStatus(429);
//            response.getWriter().write("quá nhiều yêu cầu trong 1 khoảng thười gian ngắn");
//            return false ;
//        }
//        return true ;
//    }
//}
