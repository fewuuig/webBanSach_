package com.example.webbansach_backend.service;

import com.example.webbansach_backend.dto.ProfileUserResponeDTO;
import com.example.webbansach_backend.dto.profileUser.UpdateProfileUserDTO;

public interface ProfileUserService {
    ProfileUserResponeDTO getProfileUser(String tenDangNhap) ;
    void updateProfileUser(String tenDangNhap, UpdateProfileUserDTO updateProfileUserDTO) ;
}
