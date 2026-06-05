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

    /**
     * 회원가입 로직 처리.
     * 중복된 아이디나 이메일이 있는지 검사한 후, 새로운 사용자를 DB에 저장합니다.
     * 
     * @param requestDto 가입할 사용자 정보
     * @return 가입 성공 여부 및 메시지
     */
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

    /**
     * 로그인 로직 처리.
     * 아이디 존재 여부와 비밀번호 일치 여부를 검증합니다.
     * 
     * @param requestDto 로그인 시도 정보
     * @return 로그인 성공 여부 및 메시지
     */
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

    /**
     * 사용자 프로필 조회 로직.
     * DB에서 해당 사용자의 정보를 찾아 DTO 형태로 반환합니다.
     * 
     * @param username 조회할 계정명
     * @return 사용자의 세부 프로필 정보
     */
    public UserProfileDto getUserProfile(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        User user = optionalUser.get();
        return new UserProfileDto(user.getUsername(), user.getEmail(), user.getPassword());
    }

    /**
     * 사용자 프로필 업데이트 로직.
     * 기존 사용자를 찾아 이메일과 비밀번호 등을 갱신합니다.
     * 
     * @param username 업데이트할 대상 계정명
     * @param dto 변경할 프로필 정보
     * @return 업데이트 성공 여부 및 메시지
     */
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
