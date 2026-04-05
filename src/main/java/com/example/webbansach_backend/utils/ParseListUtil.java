package com.example.webbansach_backend.utils;

import java.util.List;

public class ParseListUtil {
    public static List<Integer> toListNumber(List<Object> ids){
        return ids.stream().map(id->Integer.parseInt(id.toString())).toList() ;
    }
    public static List<String> toKeyBookInfo(List<Integer> ids , String keyInfo){
        return ids.stream().map(id ->keyInfo+id).toList() ;
    }
    public static List<String> toKeyBookInfoObj(List<Object> ids , String keyInfo){
        return ids.stream().map(id ->keyInfo+id).toList() ;
    }
}

