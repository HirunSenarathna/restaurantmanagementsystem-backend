package com.sdp.userservice.service;

import com.sdp.userservice.dto.Request.LoginRequest;
import com.sdp.userservice.dto.Response.JwtResponse;

public interface AuthService {

    JwtResponse login(LoginRequest loginRequest);
    void logout(String token);
}
