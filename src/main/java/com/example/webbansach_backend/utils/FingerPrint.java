package com.example.webbansach_backend.utils;

import jakarta.servlet.http.HttpServletRequest;

public  class FingerPrint {
    private static String getFingerPrinterClient(HttpServletRequest request){
        String fingerPrint = request.getHeader("X-Client-ID");
        if(fingerPrint == null){
            fingerPrint = request.getRemoteAddr() ;
        }
        return fingerPrint ;
    }

}
