package com.example.webbansach_backend.dto.stats;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StatTodayDTO {
    private int orders ;
    private int books ;
    private double revenue ;
}
