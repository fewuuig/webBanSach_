package com.example.webbansach_backend.utils;

import java.util.Map;
public class MapUtil{
    public static <T> T getObject(Map<String,Object> params , String key , Class<T> tClass){
        Object obj = params.get(key);

        if(obj == null) return null;

        String value = obj.toString().trim();
        if(value.isEmpty()) return null;

        try {
            if(tClass == Double.class) {
                return tClass.cast(Double.valueOf(value));
            } else if(tClass == Integer.class) {
                return tClass.cast(Integer.valueOf(value));
            } else if(tClass == String.class) {
                return tClass.cast(value);
            }
        } catch (Exception e) {
            return null; // tránh crash
        }

        return null;
    }
}
