package com.example.incubator.back.service.dto;

import com.example.incubator.back.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditUserDto {
    @NotBlank
    private String uuid;
    @NotBlank
    private String username;
    private String password;
    @NotBlank
    private String email;
    @NotNull
    private Role role;
}
