package com.example.incubator.backend.service.dto.user;

import com.example.incubator.backend.entity.user.Role;
import lombok.Data;

@Data
public class UserDto {
    private String uuid;
    private String username;
    private String password;
    private String email;
    private Role role;
}
