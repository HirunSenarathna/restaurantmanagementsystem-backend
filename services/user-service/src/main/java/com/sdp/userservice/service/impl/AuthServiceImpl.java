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
            System.out.println(loginRequest + " login in authservice");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            System.out.println("Authenticated: " + authentication.isAuthenticated() );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            System.out.println("Genarated jwt: " + jwt);

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            return JwtResponse.builder()
                    .token(jwt)
                    .id(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();  // logs full stack trace to console
            throw new BadCredentialsException("Login failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void logout(String token) {
        tokenBlacklist.addToBlacklist(token);
        SecurityContextHolder.clearContext();
    }
}
