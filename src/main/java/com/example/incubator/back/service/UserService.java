package com.example.incubator.back.service;

import com.example.incubator.back.entity.UserEntity;
import com.example.incubator.back.repo.UserRepository;
import com.example.incubator.back.service.dto.UserCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

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
}
