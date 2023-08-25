package com.example.incubator.back.service.dto;

import com.example.incubator.back.entity.user.UserEntity;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UserCredentials implements UserDetails {
    private final boolean accountNonExpired = true;
    private final boolean accountNonLocked = true;
    private final boolean credentialsNonExpired = true;
    private final boolean enabled = true;

    private final String username;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;

    public UserCredentials(UserEntity user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return new ArrayList<>(authorities);
    }
}
