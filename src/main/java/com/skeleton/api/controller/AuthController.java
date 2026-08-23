package com.skeleton.api.controller;

import com.skeleton.api.dto.request.LoginRequest;
import com.skeleton.api.dto.request.RegisterRequest;
import com.skeleton.api.dto.response.ApiResponse;
import com.skeleton.api.dto.response.JwtResponse;
import com.skeleton.api.dto.response.UserResponse;
import com.skeleton.api.security.CustomUserDetails;
import com.skeleton.api.service.AuthService;
import com.skeleton.api.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseUtil.created("User registered successfully", response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseUtil.ok("Login successful", response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal CustomUserDetails principal) {
        UserResponse response = authService.me(principal.getUsername());
        return ResponseUtil.ok("Current user fetched successfully", response);
    }
}
