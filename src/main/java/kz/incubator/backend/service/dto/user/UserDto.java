package kz.incubator.backend.service.dto.user;

import kz.incubator.backend.entity.user.Role;
import lombok.Data;

@Data
public class UserDto {
    private String uuid;
    private String username;
    private String password;
    private String email;
    private Role role;
}
