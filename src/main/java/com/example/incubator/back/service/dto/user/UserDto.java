package com.example.incubator.back.service.dto.user;

import com.example.incubator.back.entity.user.Role;
import lombok.Data;

@Data
public class UserDto {
    private String uuid;
    private String username;
    private String password;
    private String email;
    private Role role;
}
