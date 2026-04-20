package com.example.webbansach_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponeDTO {
    private String error ;
    private int status ;
    private Long timestamp ;
}
