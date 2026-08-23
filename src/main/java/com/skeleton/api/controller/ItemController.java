package com.skeleton.api.controller;

import com.skeleton.api.dto.request.ItemRequest;
import com.skeleton.api.dto.response.ApiResponse;
import com.skeleton.api.dto.response.ItemResponse;
import com.skeleton.api.dto.response.PageResponse;
import com.skeleton.api.security.CustomUserDetails;
import com.skeleton.api.service.ItemService;
import com.skeleton.api.util.PaginationUtil;
import com.skeleton.api.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ApiResponse<ItemResponse>> create(
            @Valid @RequestBody ItemRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {

        ItemResponse response = itemService.create(request, principal.getUsername());
        return ResponseUtil.created("Item created successfully", response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ItemResponse>>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {

        Pageable pageable = PaginationUtil.build(page, size, sortBy, direction);
        PageResponse<ItemResponse> result = itemService.getAll(pageable, search, category);
        return ResponseUtil.ok("Items fetched successfully", result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> getById(@PathVariable Long id) {
        ItemResponse result = itemService.getById(id);
        return ResponseUtil.ok("Item fetched successfully", result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {

        ItemResponse result = itemService.update(id, request, principal.getUsername());
        return ResponseUtil.ok("Item updated successfully", result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {

        itemService.delete(id, principal.getUsername());
        return ResponseUtil.ok("Item deleted successfully");
    }
}
