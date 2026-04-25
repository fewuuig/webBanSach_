package com.example.webbansach_backend.dto.Message;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
public class MessageResponeDTO {
    private String content ;
    private String sender ;
    private Instant timestamp ;


}
