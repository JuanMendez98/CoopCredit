package com.crudzao.creditapp.infrastructure.adapters.rest.controller;

import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.RoleEntity;
import com.crudzao.creditapp.infrastructure.adapters.jpa.entity.UserEntity;
import com.crudzao.creditapp.infrastructure.adapters.jpa.repository.RoleRepository;
import com.crudzao.creditapp.infrastructure.adapters.jpa.repository.UserRepository;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.AuthResponse;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.LoginRequest;
import com.crudzao.creditapp.infrastructure.adapters.rest.dto.RegisterRequest;
import com.crudzao.creditapp.infrastructure.config.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Authentication controller for user registration and login
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registering new user: {}", registerRequest.getUsername());

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest()
                .body("Error: Username is already taken");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                .body("Error: Email is already in use");
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(true);

        Set<RoleEntity> roles = new HashSet<>();
        RoleEntity affiliateRole = roleRepository.findByName("ROLE_AFFILIATE")
            .orElseThrow(() -> new RuntimeException("Role not found"));
        roles.add(affiliateRole);
        user.setRoles(roles);

        UserEntity savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        // Auto-login after registration
        String token = jwtTokenProvider.generateToken(
            new UsernamePasswordAuthenticationToken(savedUser.getUsername(), registerRequest.getPassword())
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new AuthResponse(token, savedUser.getUsername(), savedUser.getEmail()));
    }

    /**
     * Login user and return JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("User login attempt: {}", loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            String token = jwtTokenProvider.generateToken(authentication);
            UserEntity user = (UserEntity) authentication.getPrincipal();

            log.info("User logged in successfully: {}", user.getUsername());

            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getEmail()));
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(null, null, "Invalid username or password"));
        }
    }
}
