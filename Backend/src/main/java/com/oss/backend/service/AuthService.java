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
            return new AuthResponseDto(false, "?? ? ???.");
        }

        if (requestDto.getEmail() != null && !requestDto.getEmail().trim().isEmpty()) {
            if (userRepository.existsByEmail(requestDto.getEmail())) {
                return new AuthResponseDto(false, "?? ? ???.");
            }
        }

        User user = new User(requestDto.getUsername(), requestDto.getEmail(), requestDto.getPassword());
        userRepository.save(user);

        return new AuthResponseDto(true, "?? ?????");
    }

    public AuthResponseDto login(AuthRequestDto requestDto) {
        Optional<User> optionalUser = userRepository.findByUsername(requestDto.getUsername());

        if (optionalUser.isEmpty()) {
            return new AuthResponseDto(false, "?? ? ???.");
        }

        User user = optionalUser.get();
        if (!user.getPassword().equals(requestDto.getPassword())) {
            return new AuthResponseDto(false, "? ??? ??.");
        }

        return new AuthResponseDto(true, "???!");
    }

    public UserProfileDto getUserProfile(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("???  ????.");
        }
        User user = optionalUser.get();
        return new UserProfileDto(user.getUsername(), user.getEmail(), user.getPassword());
    }

    public AuthResponseDto updateUserProfile(String username, UserProfileDto dto) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return new AuthResponseDto(false, "???  ????.");
        }

        User user = optionalUser.get();
        // Update fields
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        
        userRepository.save(user);

        return new AuthResponseDto(true, "???? ?");
    }
}
