package com.oss.backend.controller;

import com.oss.backend.dto.AuthRequestDto;
import com.oss.backend.dto.AuthResponseDto;
import com.oss.backend.dto.UserProfileDto;
import com.oss.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@RequestBody AuthRequestDto requestDto) {
        AuthResponseDto response = authService.signup(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto requestDto) {
        AuthResponseDto response = authService.login(requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(@RequestHeader("X-Username") String username) {
        try {
            UserProfileDto profile = authService.getUserProfile(username);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<AuthResponseDto> updateProfile(
            @RequestHeader("X-Username") String username,
            @RequestBody UserProfileDto profileDto) {
        AuthResponseDto response = authService.updateUserProfile(username, profileDto);
        return ResponseEntity.ok(response);
    }
}
