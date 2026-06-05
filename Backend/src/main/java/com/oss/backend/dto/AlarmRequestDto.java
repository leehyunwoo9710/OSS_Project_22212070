package com.oss.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// [알람 설정 요청 DTO] : 클라이언트에서 넘겨주는 알람 설정 데이터 껍데기
@Getter
@Setter
@NoArgsConstructor
public class AlarmRequestDto {
    private String username;
    private LocalDateTime alarmTime;
    private boolean isDaily;
}
