package com.internship.tool.controller;

import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import com.internship.tool.util.InputSanitizer;
import com.internship.tool.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and JWT token refresh")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user with a BCrypt-hashed password and default role VIEWER."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflict — username already taken",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Username already taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        User user = new User();
        user.setUsername(InputSanitizer.sanitize(request.username()));
        user.setPassword(passwordEncoder.encode(InputSanitizer.trim(request.password())));
        user.setEmail(InputSanitizer.sanitize(request.email()));
        user.setRole("VIEWER");

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates the user with username/password and returns a JWT access token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized — invalid username or password",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            String token = jwtUtil.generateToken(authentication.getName());

            Map<String, Object> response = new HashMap<>();
            response.put("username", authentication.getName());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @Operation(
            summary = "Refresh JWT token",
            description = "Refreshes an existing valid JWT token and returns a new one."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized — invalid or expired token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Existing JWT token to refresh",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefreshRequest.class))
            )
            @RequestBody RefreshRequest request) {
        String oldToken = request.token();

        if (!jwtUtil.validateToken(oldToken)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        String username = jwtUtil.getUsernameFromToken(oldToken);
        String newToken = jwtUtil.generateToken(username);

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("token", newToken);
        return ResponseEntity.ok(response);
    }

    @Schema(name = "RegisterRequest", description = "User registration payload")
    public record RegisterRequest(
            @Schema(description = "Unique username", example = "john_doe") String username,
            @Schema(description = "Plain-text password (will be hashed)", example = "SecurePass123!") String password,
            @Schema(description = "Email address", example = "john@example.com") String email) {
    }

    @Schema(name = "LoginRequest", description = "User login payload")
    public record LoginRequest(
            @Schema(description = "Registered username", example = "john_doe") String username,
            @Schema(description = "User password", example = "SecurePass123!") String password) {
    }

    @Schema(name = "RefreshRequest", description = "Token refresh payload")
    public record RefreshRequest(
            @Schema(description = "Existing valid JWT token", example = "eyJhbGciOiJIUzI1NiIs...") String token) {
    }

    @Schema(name = "TokenResponse", description = "Authentication response containing JWT token")
    public static class TokenResponse {
        @Schema(description = "Authenticated username", example = "john_doe")
        public String username;
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIs...")
        public String token;
    }

    @Schema(name = "AuthResponse", description = "Registration response")
    public static class AuthResponse {
        @Schema(description = "Success message", example = "User registered successfully")
        public String message;
        @Schema(description = "Registered username", example = "john_doe")
        public String username;
        @Schema(description = "Assigned role", example = "VIEWER")
        public String role;
    }

    @Schema(name = "ErrorResponse", description = "Error response wrapper")
    public static class ErrorResponse {
        @Schema(description = "Error message", example = "Username already taken")
        public String error;
    }
}
