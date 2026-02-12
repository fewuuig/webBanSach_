package com.example.webbansach_backend.handler;

import com.example.webbansach_backend.dto.ErrorResponeDTO;
import com.example.webbansach_backend.exception.NotFoundException;
import com.example.webbansach_backend.exception.OutOfStockException;
import com.example.webbansach_backend.exception.VoucherStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponeDTO> handleOutOfStockException(OutOfStockException ex ){
        ErrorResponeDTO errorResponeDTO = new ErrorResponeDTO(ex.getMessage() , HttpStatus.CONFLICT.value()  , System.currentTimeMillis()) ;
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponeDTO) ;
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponeDTO> handleNotFoundException(NotFoundException ex){
        ErrorResponeDTO errorResponeDTO = new ErrorResponeDTO(ex.getMessage() , HttpStatus.NOT_FOUND.value(), System.currentTimeMillis()) ;
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponeDTO) ;
    }
    @ExceptionHandler(VoucherStateException.class)
    public ResponseEntity<ErrorResponeDTO> handleVoucherStateException(VoucherStateException ex){
        ErrorResponeDTO errorResponeDTO = new ErrorResponeDTO(ex.getMessage() , HttpStatus.CONFLICT.value(), System.currentTimeMillis()) ;
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponeDTO) ;
    }
}
