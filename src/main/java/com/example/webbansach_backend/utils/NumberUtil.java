package com.example.webbansach_backend.utils;

public class NumberUtil {
    public static boolean isInt(String target){
        try {
            Integer.parseInt(target) ;
        }catch (NumberFormatException ex){
            return false ;
        }
        return true ;
    }
    public static boolean isDouble(String target){
        try {
            Double.parseDouble(target) ;
        }catch (NumberFormatException ex) {
            return false ;
        }
        return true ;
    }


}
