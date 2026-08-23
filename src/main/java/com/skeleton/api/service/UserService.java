package com.skeleton.api.service;

import com.skeleton.api.dto.response.PageResponse;
import com.skeleton.api.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {
    PageResponse<UserResponse> getAll(Pageable pageable);

    UserResponse getById(Long id);

    void delete(Long id);
}
