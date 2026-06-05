package com.oss.backend.repository;

import com.oss.backend.entity.MedicineAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// [알람 DB 접근] : MedicineAlarm 엔티티에 대한 DB 쿼리 인터페이스
@Repository
public interface MedicineAlarmRepository extends JpaRepository<MedicineAlarm, Long> {
    
    // [발송 대상 조회] : 지정된 시간 이전이면서 아직 발송되지 않은 알람들 찾기
    List<MedicineAlarm> findByAlarmTimeBeforeAndIsSentFalse(LocalDateTime time);
    
    // [사용자 알람 조회] : 특정 사용자의 발송 대기 중인 알람을 시간순으로 찾기
    List<MedicineAlarm> findByUser_UsernameAndIsSentFalseOrderByAlarmTimeAsc(String username);
}
