package com.example.incubator.backend.service.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChangePasswordDto {
    @NotBlank
    private String newPassword;
    @NotBlank
    private String currentPassword;
}
