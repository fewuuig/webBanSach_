package com.example.webbansach_backend.security;

//import com.example.webbansach_backend.filter.AuthenticationFilter;
import com.example.webbansach_backend.security.EndPoint;
import com.example.webbansach_backend.service.UserService;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;


@Configuration

public class SecurityConfig {

//    @Autowired
//    private AuthenticationFilter authenticationFilter ;

    @Autowired
    private  UserService userService ;
;

    // mã hóa bằng Bcryp
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder() ;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserService userService) {
        DaoAuthenticationProvider dap = new DaoAuthenticationProvider();
        dap.setUserDetailsService(userService);
        dap.setPasswordEncoder(passwordEncoder());
        return dap;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception {
        http.authorizeHttpRequests(
                configurer->configurer

                        .requestMatchers(HttpMethod.GET, EndPoint.PUBLIC_GET_ENDPOINS).permitAll()
                        .requestMatchers(HttpMethod.POST , EndPoint.PUBLIC_POST_ENDPOINS ).permitAll()
                        .requestMatchers(HttpMethod.GET , EndPoint.ADMIN_GET_ENDPOINS).hasAnyAuthority("ADMIN" , "STAFF" ,"USER")
                        .requestMatchers(HttpMethod.POST ,EndPoint.ADMIN_POST_ENDPOINS).hasAuthority("ADMIN")

        );
        http.cors(cors->{
            cors.configurationSource(request -> {
                CorsConfiguration corsConfig = new CorsConfiguration() ;
                corsConfig.addAllowedOrigin("*");
                corsConfig.setAllowedMethods(Arrays.asList("GET", "POST" , "PUT" , "DELETE"));
                corsConfig.addAllowedHeader("*");
                return corsConfig;
            });
        }) ;

//
//        http.addFilterBefore(authenticationFilter , UsernamePasswordAuthenticationFilter.class) ;
//        http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) ;
//        http.authenticationProvider(daoAuthenticationProvider()) ;
        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf->csrf.disable());
        return http.build() ;
    }
    @Bean
    public AuthenticationManager authorizationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager() ;
    }

}
