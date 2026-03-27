package com.example.webbansach_backend.utils;

import com.example.webbansach_backend.Entity.NguoiDung;

import java.util.HashSet;
import java.util.Set;

public class CheckRoleUItil {
    public static boolean checkRole(NguoiDung nguoiDung){
        Set<String> role = new HashSet<>() ;
        nguoiDung.getNguoiDungQuyens().forEach(ndq->{
            role.add(ndq.getQuyen().getTenQuyen()) ;
        });
        if(!role.isEmpty()){
            if(!role.contains("ADMIN")) return false ;
        }else return false ;

        return true ;
    }
    public static boolean checkRoleAdminOrUser(NguoiDung nguoiDung){
        Set<String> role = new HashSet<>() ;
        nguoiDung.getNguoiDungQuyens().forEach(ndq->{
            role.add(ndq.getQuyen().getTenQuyen()) ;
        });
        if(!role.isEmpty()){
            if(!role.contains("ADMIN") && !role.contains("STAFF")) return false ;
        }else return false ;

        return true ;
    }
}
