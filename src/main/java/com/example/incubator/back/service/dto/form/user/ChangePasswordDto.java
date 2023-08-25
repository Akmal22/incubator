package com.example.incubator.back.service.dto.form.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChangePasswordDto {
    private String newPassword;
    private String currentPassword;
}
