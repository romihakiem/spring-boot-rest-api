package com.skeleton.api.service.impl;

import com.skeleton.api.dto.request.LoginRequest;
import com.skeleton.api.dto.request.RegisterRequest;
import com.skeleton.api.dto.response.JwtResponse;
import com.skeleton.api.dto.response.UserResponse;
import com.skeleton.api.entity.Role;
import com.skeleton.api.entity.User;
import com.skeleton.api.exception.BadRequestException;
import com.skeleton.api.exception.ResourceNotFoundException;
import com.skeleton.api.repository.UserRepository;
import com.skeleton.api.service.AuthService;
import com.skeleton.api.util.JwtUtil;
import com.skeleton.api.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordUtil.hash(request.getPassword()))
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return JwtResponse.builder()
                .token(token)
                .expiresIn(jwtUtil.getExpirationMs())
                .user(UserResponse.fromEntity(user))
                .build();
    }

    @Override
    public UserResponse me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.fromEntity(user);
    }
}
