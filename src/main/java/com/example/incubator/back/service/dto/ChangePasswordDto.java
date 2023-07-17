package com.example.incubator.back.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChangePasswordDto {
    private String newPassword;
    private String currentPassword;
}
