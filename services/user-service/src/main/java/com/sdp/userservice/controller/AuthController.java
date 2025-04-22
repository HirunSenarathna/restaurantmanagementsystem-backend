package com.sdp.userservice.controller;


import com.sdp.userservice.dto.Request.LoginRequest;
import com.sdp.userservice.dto.Response.JwtResponse;
import com.sdp.userservice.entity.Customer;
import com.sdp.userservice.entity.Employee;
import com.sdp.userservice.entity.User;
import com.sdp.userservice.entity.Role;
import com.sdp.userservice.security.JwtTokenProvider;
import com.sdp.userservice.service.AuthService;
import com.sdp.userservice.service.UserService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println(loginRequest);
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = tokenProvider.resolveToken(authHeader);
        if (token != null) {
            authService.logout(token);
            return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
    }


}
