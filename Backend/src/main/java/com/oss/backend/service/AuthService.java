package com.oss.backend.service;

import com.oss.backend.dto.AuthRequestDto;
import com.oss.backend.dto.AuthResponseDto;
import com.oss.backend.dto.UserProfileDto;
import com.oss.backend.entity.User;
import com.oss.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponseDto signup(AuthRequestDto requestDto) {
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            return new AuthResponseDto(false, "이미 존재하는 아이디입니다.");
        }

        if (requestDto.getEmail() != null && !requestDto.getEmail().trim().isEmpty()) {
            if (userRepository.existsByEmail(requestDto.getEmail())) {
                return new AuthResponseDto(false, "이미 존재하는 이메일입니다.");
            }
        }

        User user = new User(requestDto.getUsername(), requestDto.getEmail(), requestDto.getPassword());
        userRepository.save(user);

        return new AuthResponseDto(true, "회원가입 성공");
    }

    public AuthResponseDto login(AuthRequestDto requestDto) {
        Optional<User> optionalUser = userRepository.findByUsername(requestDto.getUsername());

        if (optionalUser.isEmpty()) {
            return new AuthResponseDto(false, "존재하지 않는 아이디입니다.");
        }

        User user = optionalUser.get();
        if (!user.getPassword().equals(requestDto.getPassword())) {
            return new AuthResponseDto(false, "비밀번호가 일치하지 않습니다.");
        }

        return new AuthResponseDto(true, "로그인 성공");
    }

    public UserProfileDto getUserProfile(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        User user = optionalUser.get();
        return new UserProfileDto(user.getUsername(), user.getEmail(), user.getPassword());
    }

    public AuthResponseDto updateUserProfile(String username, UserProfileDto dto) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return new AuthResponseDto(false, "사용자를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();
        // Update fields
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        
        userRepository.save(user);

        return new AuthResponseDto(true, "프로필 수정 완료");
    }
}
