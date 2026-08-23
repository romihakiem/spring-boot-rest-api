package com.skeleton.api.util;

import com.skeleton.api.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Shorthand helpers so controllers don't repeat ResponseEntity + ApiResponse boilerplate.
 */
public final class ResponseUtil {
    private ResponseUtil() {
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message) {
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> noContent(String message) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResponse.error(message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message, Object errors) {
        return ResponseEntity.status(status).body(ApiResponse.error(message, errors));
    }
}
