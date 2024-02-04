package kz.incubator.backend.service.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {
    private final AuthenticationContext authenticationContext;

    public UserDetails getAuthenticationContext() {
        return this.authenticationContext.getAuthenticatedUser(UserDetails.class).get();
    }

    public void logout() {
        this.authenticationContext.logout();
    }
}
