package com.example.webbansach_backend.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeLogUtil {
    public static String toTimeSystemLog(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss" )) ;

    }
}
