package com.springstore.auth;

import com.springstore.auth.dto.AuthResponse;
import com.springstore.auth.dto.LoginRequest;
import com.springstore.user.UserRepository;
import com.springstore.user.dto.UserRequest;
import com.springstore.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a user with USER role and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already in use")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRequest req) {
        var userResponse = userService.create(req);
        var token = jwtService.generateToken(userResponse.id(), "USER");
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Validates credentials and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        var user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        var token = jwtService.generateToken(user.getId(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
