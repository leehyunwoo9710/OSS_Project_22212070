package com.oss.backend.controller;

import com.oss.backend.dto.AlarmRequestDto;
import com.oss.backend.entity.User;
import com.oss.backend.repository.UserRepository;
import com.oss.backend.service.NotificationScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oss.backend.dto.AlarmResponseDto;
import com.oss.backend.entity.MedicineAlarm;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationScheduler notificationScheduler;
    private final UserRepository userRepository;

    // [알림 등록] : 사용자 알림 시간 및 반복 여부 저장
    @PostMapping("/set")
    public ResponseEntity<String> setAlarm(@RequestBody AlarmRequestDto requestDto) {
        Optional<User> optionalUser = userRepository.findByUsername(requestDto.getUsername());
        
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = optionalUser.get();
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("User has no registered email");
        }

        notificationScheduler.setAlarm(user, requestDto.getAlarmTime(), requestDto.isDaily());
        return ResponseEntity.ok("Alarm successfully set for " + requestDto.getAlarmTime() + (requestDto.isDaily() ? " (Daily)" : ""));
    }

    // [목록 조회] : 현재 대기 중인 사용자의 알림 리스트 반환
    @GetMapping("/list")
    public ResponseEntity<List<AlarmResponseDto>> getAlarms(@RequestHeader("X-Username") String username) {
        List<MedicineAlarm> alarms = notificationScheduler.getUserAlarms(username);
        List<AlarmResponseDto> response = alarms.stream()
                .map(a -> new AlarmResponseDto(a.getId(), a.getAlarmTime(), a.isDaily()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // [알림 삭제] : 알림 식별자(ID)를 이용해 해당 알림 취소
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable Long id, @RequestHeader("X-Username") String username) {
        notificationScheduler.deleteAlarm(id, username);
        return ResponseEntity.noContent().build();
    }
}
