package org.example.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register new user",description = "Adds new user to database")
    public ResponseEntity<?> registerUser(@Parameter(description = "User object",required = true)@Valid @RequestBody User user) {
        try {
            user.setRole(User.Role.USER);
            User registeredUser = userService.registerUser(user);

            registeredUser.setPassword(null);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/admin")
    @Operation(summary = "Register new admin",description = "Adds new admin to database",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> registerAdmin(@Parameter(description = "User object (admin role)",required = true)@Valid @RequestBody User user) {
        try {
            user.setRole(User.Role.ADMIN);
            User registeredUser = userService.registerUser(user);

            registeredUser.setPassword(null);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    @Operation  (summary = "Get current user",description = "Returns current user")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok().build();
    }
}