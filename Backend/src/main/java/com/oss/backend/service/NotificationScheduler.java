package com.oss.backend.service;

import com.oss.backend.entity.MedicineAlarm;
import com.oss.backend.entity.User;
import com.oss.backend.repository.MedicineAlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final MedicineAlarmRepository alarmRepository;
    private final JavaMailSender mailSender;

    // [알람 설정] : 사용자, 시간, 매일 반복 여부를 DB에 저장
    @Transactional
    public void setAlarm(User user, LocalDateTime time, boolean isDaily) {
        MedicineAlarm alarm = new MedicineAlarm(user, time, isDaily);
        alarmRepository.save(alarm);
        log.info("Alarm set for user {} at {}", user.getUsername(), time);
    }

    // [알람 조회] : 사용자의 미발송 상태인 현재 알람 목록 가져오기
    public List<MedicineAlarm> getUserAlarms(String username) {
        return alarmRepository.findByUser_UsernameAndIsSentFalseOrderByAlarmTimeAsc(username);
    }

    // [알람 삭제] : ID로 조회 후 해당 사용자의 알람이 맞으면 삭제 처리
    @Transactional
    public void deleteAlarm(Long alarmId, String username) {
        alarmRepository.findById(alarmId).ifPresent(alarm -> {
            if (alarm.getUser().getUsername().equals(username)) {
                alarmRepository.delete(alarm);
            }
        });
    }

    // [주기적 발송 처리] : 1분마다 실행되어 알람 시간이 지난 메일 발송 및 날짜 연기
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void triggerEmail() {
        LocalDateTime now = LocalDateTime.now();
        List<MedicineAlarm> pendingAlarms = alarmRepository.findByAlarmTimeBeforeAndIsSentFalse(now);

        for (MedicineAlarm alarm : pendingAlarms) {
            boolean success = sendEmail(alarm);
            if (success) {
                if (alarm.isDaily()) {
                    alarm.setAlarmTime(alarm.getAlarmTime().plusDays(1));
                    log.info("Daily alarm rescheduled for user {} to {}", alarm.getUser().getUsername(), alarm.getAlarmTime());
                } else {
                    alarm.setSent(true);
                }
                alarmRepository.save(alarm);
                log.info("Email successfully sent to {}", alarm.getUser().getEmail());
            } else {
                log.error("Failed to send email to {}", alarm.getUser().getEmail());
            }
        }
    }

    // [이메일 전송 로직] : SimpleMailMessage를 이용해 실제 메일 발송
    private boolean sendEmail(MedicineAlarm alarm) {
        User user = alarm.getUser();
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("[Medicine Helper] 약 복용 시간 알림 💊");
            
            String content = user.getUsername() + " 님 설정하신 약의 복용시간입니다!" + (alarm.isDaily() ? " (매일 반복)" : "");
            message.setText(content);
            
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("Error sending email: ", e);
            return false;
        }
    }
}
