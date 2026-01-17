package com.example.webbansach_backend.security;

public class JwtRespone {
    private final String jwt ;

    public JwtRespone(String jwt){
        this.jwt = jwt ;
    }

    public String getJWT(){
        return jwt ;
    }


}
