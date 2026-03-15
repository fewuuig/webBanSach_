package com.example.webbansach_backend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.stream.MapRecord;

public class ParseJacksonUtil {
    public static String toString(String value){
        try {
            ObjectMapper objectMapper = new ObjectMapper() ;
            value = objectMapper.readValue(value , String.class) ;
            return value ;
        }catch (Exception ex){
            System.out.println("quá trình chuyển đổi từ jackson sang String thuần bị sai");
            throw new RuntimeException("loi trong qua trinh parse sang string thuan");

        }

    }
}
