package com.skeleton.api.service;

import com.skeleton.api.dto.request.LoginRequest;
import com.skeleton.api.dto.request.RegisterRequest;
import com.skeleton.api.dto.response.JwtResponse;
import com.skeleton.api.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);

    JwtResponse login(LoginRequest request);

    UserResponse me(String email);
}
