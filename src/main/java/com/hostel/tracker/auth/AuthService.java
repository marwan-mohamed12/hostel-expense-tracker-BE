package com.hostel.tracker.auth;

import com.hostel.tracker.common.ApiException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository users;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            AppUserRepository users,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.users = users;
        this.jwtService = jwtService;
    }

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        AppUser user = users.findByUsernameIgnoreCase(request.username())
                .orElseThrow(() -> ApiException.unauthorized("Invalid username or password"));
        return new AuthDtos.LoginResponse(
                jwtService.createToken(user),
                jwtService.expiresAt().toString(),
                AuthDtos.UserResponse.from(user)
        );
    }

    public AuthDtos.UserResponse me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw ApiException.unauthorized("Not authenticated");
        }
        AppUser user = users.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> ApiException.unauthorized("Not authenticated"));
        return AuthDtos.UserResponse.from(user);
    }
}
