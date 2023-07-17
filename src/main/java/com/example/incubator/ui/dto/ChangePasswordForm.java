package com.example.incubator.ui.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordForm {
    @NotBlank
    private String newPassword;
    @NotBlank
    private String repeatPassword;
    @NotBlank
    private String currentPassword;
}
