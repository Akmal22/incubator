package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.user.Role;
import com.example.incubator.backend.entity.user.UserEntity;
import com.example.incubator.backend.repo.UserRepository;
import com.example.incubator.backend.service.dto.ServiceResult;
import com.example.incubator.backend.service.dto.user.ChangePasswordDto;
import com.example.incubator.backend.service.dto.user.UserCredentials;
import com.example.incubator.backend.service.dto.user.UserDto;
import com.example.incubator.backend.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
                    log.error("User with username {} not found", username);
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

    public List<UserDto> getAllUsers(String filterText) {
        return userRepository.findAllByFilterText(filterText).stream()
                .map(UserService::convertUserEntity)
                .collect(Collectors.toList());
    }

    public List<UserDto> getAllManagersPageable(Pageable pageable) {
        return userRepository.findAllByRole(Role.ROLE_BI_MANAGER, pageable).stream()
                .map(UserService::convertUserEntity)
                .collect(Collectors.toList());
    }

    public UserDto getManager(String name) {
        return userRepository.findByUsernameAndRole(name, Role.ROLE_BI_MANAGER)
                .map(UserService::convertUserEntity)
                .orElseThrow(() -> {
                    log.error("Manager with login {} not found", name);
                    throw new IllegalArgumentException("User not found");
                });
    }

    public ServiceResult deleteUser(UserDto userDto) {
        Optional<UserEntity> optionalUser = userRepository.findByUuid(userDto.getUuid());
        if (optionalUser.isEmpty()) {
            log.error("Error while deleting user, user with uuid [{}] does not exist", userDto.getUuid());
            return new ServiceResult(false, "User does not exist");
        }

        userRepository.delete(optionalUser.get());
        return new ServiceResult(true, null);
    }

    public ServiceResult updateUser(UserDto userDto) {
        if (isNotBlank(userDto.getPassword())) {
            String encodedPassword = bCryptPasswordEncoder.encode(userDto.getPassword());
            userRepository.updateUser(userDto.getEmail(), userDto.getRole(), encodedPassword, userDto.getUuid());
        } else {
            userRepository.updateUser(userDto.getEmail(), userDto.getRole(), userDto.getUuid());
        }
        return new ServiceResult(true, null);
    }

    public ServiceResult createUser(UserDto userDto) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(userDto.getUsername());
        if (userEntityOptional.isPresent()) {
            log.error("Error while creating new user, user with username [{}] already exists", userDto.getUsername());
            return new ServiceResult(false, "User with specified username already exists");
        }

        if (isBlank(userDto.getPassword())) {
            log.error("Error while creating new user, empty password");
            return new ServiceResult(false, "Password must not be blank");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setUsername(userDto.getUsername());
        userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setEmail(userDto.getEmail());
        userEntity.setRole(userDto.getRole());
        userRepository.save(userEntity);

        return new ServiceResult(true, null);
    }

    public static UserDto convertUserEntity(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setUuid(user.getUuid());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());

        return userDto;
    }
}
