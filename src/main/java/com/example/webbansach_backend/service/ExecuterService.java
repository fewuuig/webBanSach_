package com.example.webbansach_backend.service;

import org.springframework.boot.ApplicationArguments;

public interface ExecuterService {
    void taskConsumeMessage(int shard) ;
    void run(ApplicationArguments args) throws Exception ;
}
