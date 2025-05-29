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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/{id}")
    @Operation(
        summary = "Get user by id",
        description = "Returns user with assigned id.",
        security = @SecurityRequirement(name = "basicAuth")
    )
    public ResponseEntity<?> getUserById(@Parameter(description = "ID of the user",required = true) @PathVariable Long id) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get_username/{username}")
    @Operation(summary = "Get user by username",description = "Returns user with assigned username",        
            security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> getUserByUsername(@Parameter(description = "Username of the user",required = true)@PathVariable String username) {
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    @Operation(summary = "Get all users",description = "Returns list of all users in database",
            security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();

        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    @Operation(summary = "Update user by id",description = "Updates information in database about user witch matching id",
            security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> updateUser(@Parameter(description = "ID of the user",required = true) @PathVariable Long id, @Valid @RequestBody User user) {
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            updatedUser.setPassword(null);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete user by id",description = "Deletes information in database about user with matching id",
            security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> deleteUser(@Parameter(description = "ID of the user",required = true) @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/exists_username/{username}")
    @Operation(summary = "Chcecks if user exists by username ",description = "Returns existence of the user by id",
            security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> existsByUsername(@Parameter(description = "ID of the user",required = true) @PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/exists_email/{email}")
    @Operation(summary = "Chcecks if user exists by email",description = "Returns existence of the user by email",
            security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> existsByEmail(@Parameter(description = "ID of the user",required = true) @PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
