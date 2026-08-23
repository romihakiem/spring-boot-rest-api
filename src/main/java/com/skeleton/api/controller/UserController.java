package com.skeleton.api.controller;

import com.skeleton.api.dto.response.ApiResponse;
import com.skeleton.api.dto.response.PageResponse;
import com.skeleton.api.dto.response.UserResponse;
import com.skeleton.api.service.UserService;
import com.skeleton.api.util.PaginationUtil;
import com.skeleton.api.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only endpoints for managing users. Access is restricted at the
 * SecurityConfig level (hasRole("ADMIN")).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {

        Pageable pageable = PaginationUtil.build(page, size, sortBy, direction);
        PageResponse<UserResponse> result = userService.getAll(pageable);
        return ResponseUtil.ok("Users fetched successfully", result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        UserResponse result = userService.getById(id);
        return ResponseUtil.ok("User fetched successfully", result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseUtil.ok("User deleted successfully");
    }
}
