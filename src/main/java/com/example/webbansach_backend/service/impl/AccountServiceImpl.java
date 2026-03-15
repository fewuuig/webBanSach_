package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Enum.CheckAccount;
import com.example.webbansach_backend.service.AccountService;
import com.example.webbansach_backend.utils.ParseJacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private RedisTemplate<String , Object> redisTemplate ;

    // check ussername
    private boolean checkAccount(CheckAccount checkAccount, String target){
        String cache = null ;
        try {
            if(checkAccount == CheckAccount.USERNAME) {

                cache = redisTemplate.opsForValue().get("username:" + target).toString();
            }
            else if(checkAccount == CheckAccount.EMAIL) cache = redisTemplate.opsForValue().get("email:"+target).toString() ;
        }catch (Exception ex){
            return false ;
        }
        if(cache.equals(target)) return true ;
        return false ;
    }


    // check ussernmae cos toonf taij khoong
    public boolean checkUsername(String username){
        return checkAccount(CheckAccount.USERNAME , username) ;
    }

    // check email khi đăng ký tài khoản
    public boolean checkEmail(String email){
        return checkAccount(CheckAccount.EMAIL , email) ;
    }
}
