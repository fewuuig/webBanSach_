//package com.example.webbansach_backend.config;
//
//import com.example.webbansach_backend.Entity.TheLoai;
//import jakarta.persistence.EntityManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
//import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
//import org.springframework.http.HttpMethod;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//
//@Configuration
//public class MethodRestConfig implements RepositoryRestConfigurer {
//    private String url = "http://localhost:3000" ;
//    @Override
//    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
//        HttpMethod[] disableMethod = {
//                HttpMethod.DELETE ,
//                HttpMethod.PATCH,
//                HttpMethod.POST ,
//                HttpMethod.PUT
//        } ;
//        // CORS configuration
//        cors.addMapping("/**").allowedOrigins(url).allowedMethods("GET" , "POST" , "PUT" , "DELETE") ;
//        disableHttpMethods(TheLoai.class,config , disableMethod);
//    }
//
//    // config disable Method
//    private  void disableHttpMethods(Class c , RepositoryRestConfiguration config ,HttpMethod[] methods) {
//        config.getExposureConfiguration().forDomainType(c).withItemExposure((metdata, httpMethods) -> httpMethods.disable(methods)).withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(methods)) ;
//    }
//
//}
