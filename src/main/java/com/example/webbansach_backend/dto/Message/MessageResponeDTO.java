package com.example.webbansach_backend.dto.Message;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class MessageResponeDTO {
    private String content ;
    private String sender ;
    private Instant timestamp ;

    @Override
    public String toString() {
        return content +" "+ sender + " " + timestamp ;
    }
}
