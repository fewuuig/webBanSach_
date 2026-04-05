package com.example.webbansach_backend.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePassword {
    private String oldPassword ;
    private String newPassword ;
}
