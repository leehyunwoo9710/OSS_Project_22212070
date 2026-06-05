package com.oss.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// [알람 목록 응답 DTO] : 클라이언트에게 넘겨주는 알람 데이터 껍데기 (무한 참조 방지)
@Getter
@AllArgsConstructor
public class AlarmResponseDto {
    private Long id;
    private LocalDateTime alarmTime;
    private boolean isDaily;
}
