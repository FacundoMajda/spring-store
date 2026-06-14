package com.springstore.user;

import com.springstore.user.dto.UserRequest;
import com.springstore.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management (admin only)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List all users", description = "Requires ADMIN role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User list returned"),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required")
    })
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Requires ADMIN role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a user", description = "Used internally by AuthController. Creates a user with the specified role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already in use")
    })
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(req));
    }
}
