package com.favian.bank_test_case.controller;

import com.favian.bank_test_case.dto.UpdateUserRequest;
import com.favian.bank_test_case.dto.UserResponse;
import com.favian.bank_test_case.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Admin - Users", description = "User management endpoints for administrators")
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Get all users in the system with pagination (Admin only)")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort by field")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)")
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Get specific user details (Admin only)")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID")
            @PathVariable @Positive(message = "User ID must be positive") Long userId
    ) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user", description = "Update user information (Admin only)")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID")
            @PathVariable @Positive(message = "User ID must be positive") Long userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Delete a user from the system (Admin only)")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID")
            @PathVariable @Positive(message = "User ID must be positive") Long userId
    ) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
