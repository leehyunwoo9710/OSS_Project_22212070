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

    /**
     * 알림 등록 API.
     * 클라이언트로부터 요청받은 알람 정보를 기반으로 새로운 약 복용 알람을 설정합니다.
     * 
     * @param requestDto 설정할 알람 정보 (사용자명, 알람 시간, 매일 반복 여부)
     * @return 알림 설정 성공 여부 및 결과 메시지
     */
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
        return ResponseEntity.ok(
                "Alarm successfully set for " + requestDto.getAlarmTime() + (requestDto.isDaily() ? " (Daily)" : ""));
    }

    /**
     * 알림 목록 조회 API.
     * 특정 사용자가 설정해둔 미발송 상태의 대기 중인 알람 목록을 반환합니다.
     * 
     * @param username 조회할 사용자 계정명 (헤더에서 추출)
     * @return 대기 중인 알람 리스트 응답 DTO
     */
    @GetMapping("/list")
    public ResponseEntity<List<AlarmResponseDto>> getAlarms(@RequestHeader("X-Username") String username) {
        List<MedicineAlarm> alarms = notificationScheduler.getUserAlarms(username);
        List<AlarmResponseDto> response = alarms.stream()
                .map(a -> new AlarmResponseDto(a.getId(), a.getAlarmTime(), a.isDaily()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 알림 삭제 API.
     * 알림 식별자(ID)를 사용하여 기존에 설정된 알람을 취소 및 삭제합니다.
     * 
     * @param id       삭제할 알람의 고유 ID
     * @param username 요청자의 사용자 계정명 (권한 확인용)
     * @return 처리에 성공할 경우 HTTP 204 No Content 반환
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable Long id, @RequestHeader("X-Username") String username) {
        notificationScheduler.deleteAlarm(id, username);
        return ResponseEntity.noContent().build();
    }
}
