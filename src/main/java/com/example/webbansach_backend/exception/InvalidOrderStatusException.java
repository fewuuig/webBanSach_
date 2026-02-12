package com.example.webbansach_backend.exception;

public class InvalidOrderStatusException extends RuntimeException{
    public InvalidOrderStatusException(String message){
        super(message);
    }
}
