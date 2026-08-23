package com.skeleton.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {
    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long expiresIn; // milliseconds until expiry

    private UserResponse user;
}
