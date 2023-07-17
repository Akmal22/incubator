package com.example.incubator.back.service;

import com.example.incubator.back.entity.UserEntity;
import com.example.incubator.back.repo.UserRepository;
import com.example.incubator.back.service.dto.ChangePasswordDto;
import com.example.incubator.back.service.dto.ServiceResult;
import com.example.incubator.back.service.dto.UserCredentials;
import com.example.incubator.back.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecurityService securityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        return optionalUser
                .map(UserCredentials::new)
                .orElseThrow(() -> {
                    log.debug("User with username {} not found", username);
                    throw new UsernameNotFoundException("Username or password is invalid");
                });
    }

    public ServiceResult changePassword(ChangePasswordDto changePasswordDto) {
        UserDetails userDetails = securityService.getAuthenticationContext();
        String encryptedNewPassword = bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword());
        if (!bCryptPasswordEncoder.matches(changePasswordDto.getCurrentPassword(), userDetails.getPassword())) {
            log.warn("Error while resetting password, entered incorrect password for user with login {}", userDetails.getUsername());
            return new ServiceResult(false, "Invalid password");
        }

        userRepository.updateUserPassword(encryptedNewPassword, userDetails.getUsername());
        log.debug("New password for user [{}] setted", userDetails.getUsername());
        securityService.logout();
        return new ServiceResult(true, "false");
    }
}
