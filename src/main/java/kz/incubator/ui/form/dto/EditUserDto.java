package kz.incubator.ui.form.dto;

import kz.incubator.backend.entity.user.Role;
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
