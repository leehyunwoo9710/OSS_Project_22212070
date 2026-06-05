package com.oss.backend.controller;

import com.oss.backend.dto.AuthRequestDto;
import com.oss.backend.dto.AuthResponseDto;
import com.oss.backend.dto.UserProfileDto;
import com.oss.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 회원가입 API.
     * 새로운 사용자를 등록하고 결과를 반환합니다.
     * 
     * @param requestDto 회원가입 요청 정보 (아이디, 이메일, 비밀번호 등)
     * @return 등록된 사용자 정보와 인증 상태
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@RequestBody AuthRequestDto requestDto) {
        AuthResponseDto response = authService.signup(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 API.
     * 사용자 자격 증명을 확인하고 로그인 성공 응답을 반환합니다.
     * 
     * @param requestDto 로그인 요청 정보 (아이디, 비밀번호)
     * @return 로그인된 사용자 정보와 인증 상태
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto requestDto) {
        AuthResponseDto response = authService.login(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 프로필 조회 API.
     * 헤더의 사용자 이름을 기반으로 해당 사용자의 상세 프로필을 가져옵니다.
     * 
     * @param username 조회할 사용자 계정명 (헤더에서 추출)
     * @return 사용자 프로필 DTO
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(@RequestHeader("X-Username") String username) {
        try {
            UserProfileDto profile = authService.getUserProfile(username);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 사용자 프로필 수정 API.
     * 이메일, 키, 몸무게 등 사용자 개인 정보를 업데이트합니다.
     * 
     * @param username 정보를 업데이트할 사용자 계정명 (헤더에서 추출)
     * @param profileDto 변경할 프로필 데이터
     * @return 변경 처리 후 응답 정보
     */
    @PutMapping("/profile")
    public ResponseEntity<AuthResponseDto> updateProfile(
            @RequestHeader("X-Username") String username,
            @RequestBody UserProfileDto profileDto) {
        AuthResponseDto response = authService.updateUserProfile(username, profileDto);
        return ResponseEntity.ok(response);
    }
}
