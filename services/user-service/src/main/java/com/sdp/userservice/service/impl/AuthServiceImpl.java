package com.sdp.userservice.service.impl;

import com.sdp.userservice.dto.Request.LoginRequest;
import com.sdp.userservice.dto.Response.JwtResponse;
import com.sdp.userservice.entity.User;
import com.sdp.userservice.repository.UserRepository;
import com.sdp.userservice.security.JwtTokenProvider;
import com.sdp.userservice.security.TokenBlacklist;
import com.sdp.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TokenBlacklist tokenBlacklist;


    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        try {
            // Find user by identifier (username, email, or mobile number)
            User user = userRepository.findByUsername(loginRequest.getIdentifier())
                    .or(() -> userRepository.findByEmail(loginRequest.getIdentifier()))
                    .or(() -> userRepository.findByPhone(loginRequest.getIdentifier()))
                    .orElseThrow(() -> new BadCredentialsException("User not found with the provided identifier"));

            System.out.println(user);

            // Authenticate using the username and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            return JwtResponse.builder()
                    .token(jwt)
                    .id(user.getId())
                    .username(user.getUsername())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .address(user.getAddress())
                    .role(user.getRole())
                    .build();

        } catch (Exception e) {
            throw new BadCredentialsException("Login failed: " + e.getMessage(), e);
        }


    }

    @Override
    public void logout(String token) {
        tokenBlacklist.addToBlacklist(token);
        SecurityContextHolder.clearContext();
    }
}
