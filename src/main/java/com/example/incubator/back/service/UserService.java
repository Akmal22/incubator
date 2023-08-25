package com.example.incubator.back.service;

import com.example.incubator.back.entity.user.UserEntity;
import com.example.incubator.back.repo.UserRepository;
import com.example.incubator.back.service.dto.ChangePasswordDto;
import com.example.incubator.back.service.dto.EditUserDto;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

    public List<EditUserDto> getAllUsers(String filterText) {
        return userRepository.findAllByFilterText(filterText).stream()
                .map(this::convertUserEntity)
                .collect(Collectors.toList());
    }

    public ServiceResult deleteUser(EditUserDto editUserDto) {
        Optional<UserEntity> optionalUser = userRepository.findByUuid(editUserDto.getUuid());
        if (optionalUser.isEmpty()) {
            log.error("Error while deleting user, user with uuid [{}] does not exist", editUserDto.getUuid());
            return new ServiceResult(false, "User does not exist");
        }

        userRepository.delete(optionalUser.get());
        return new ServiceResult(true, null);
    }

    public ServiceResult updateUser(EditUserDto editUserDto) {
        if (isNotBlank(editUserDto.getPassword())) {
            String encodedPassword = bCryptPasswordEncoder.encode(editUserDto.getPassword());
            userRepository.updateUser(editUserDto.getEmail(), editUserDto.getRole(), encodedPassword, editUserDto.getUuid());
        } else {
            userRepository.updateUser(editUserDto.getEmail(), editUserDto.getRole(), editUserDto.getUuid());
        }
        return new ServiceResult(true, null);
    }

    public ServiceResult createUser(EditUserDto editUserDto) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(editUserDto.getUsername());
        if (userEntityOptional.isPresent()) {
            log.error("Error while creating new user, user with username [{}] already exists", editUserDto.getUsername());
            return new ServiceResult(false, "User with specified username already exists");
        }


        if (isBlank(editUserDto.getPassword())) {
            log.error("Error while creating new user, empty password");
            return new ServiceResult(false, "Password must not be blank");
        }


        UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setUsername(editUserDto.getUsername());
        userEntity.setPassword(bCryptPasswordEncoder.encode(editUserDto.getPassword()));
        userEntity.setEmail(editUserDto.getEmail());
        userEntity.setRole(editUserDto.getRole());
        userRepository.save(userEntity);


        return new ServiceResult(true, null);
    }

    private EditUserDto convertUserEntity(UserEntity user) {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setUuid(user.getUuid());
        editUserDto.setUsername(user.getUsername());
        editUserDto.setEmail(user.getEmail());
        editUserDto.setRole(user.getRole());

        return editUserDto;
    }
}
