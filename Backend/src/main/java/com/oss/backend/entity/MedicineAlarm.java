package com.oss.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// [알람 엔티티] : 데이터베이스에 저장되는 알람 정보 구조
@Entity
@Table(name = "medicine_alarms")
@Getter
@Setter
@NoArgsConstructor
public class MedicineAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [유저 맵핑] : 알람을 설정한 사용자 정보 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // [알람 시간] : 발송되어야 할 시간
    @Column(nullable = false)
    private LocalDateTime alarmTime;

    // [발송 여부] : 1회성 알람의 경우 발송 완료 여부 플래그
    @Column(nullable = false)
    private boolean isSent = false;

    // [매일 반복 여부] : 매일 같은 시간에 알람을 울릴지 여부
    @Column(nullable = false)
    private boolean isDaily = false;

    public MedicineAlarm(User user, LocalDateTime alarmTime, boolean isDaily) {
        this.user = user;
        this.alarmTime = alarmTime;
        this.isDaily = isDaily;
        this.isSent = false;
    }
}
