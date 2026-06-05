package com.oss.backend.service;

import com.oss.backend.entity.MedicineAlarm;
import com.oss.backend.entity.User;
import com.oss.backend.repository.MedicineAlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final MedicineAlarmRepository alarmRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.mail.script-url}")
    private String scriptUrl;

    /**
     * 새로운 알람을 설정합니다.
     * 지정된 사용자의 알람 시간 및 매일 반복 여부를 데이터베이스에 저장합니다.
     * 
     * @param user    알람을 설정할 사용자 엔티티
     * @param time    설정할 알람 시간
     * @param isDaily 매일 반복 여부
     */
    @Transactional
    public void setAlarm(User user, LocalDateTime time, boolean isDaily) {
        MedicineAlarm alarm = new MedicineAlarm(user, time, isDaily);
        alarmRepository.save(alarm);
        log.info("Alarm set for user {} at {}", user.getUsername(), time);
    }

    /**
     * 특정 사용자의 대기 중인 알람 목록을 조회합니다.
     * 발송되지 않은 알람들만 시간 오름차순으로 정렬하여 반환합니다.
     * 
     * @param username 조회할 사용자 계정명
     * @return 대기 중인 알람 리스트
     */
    public List<MedicineAlarm> getUserAlarms(String username) {
        return alarmRepository.findByUser_UsernameAndIsSentFalseOrderByAlarmTimeAsc(username);
    }

    /**
     * 설정된 알람을 삭제합니다.
     * 보안을 위해 삭제를 요청한 사용자와 알람의 소유자가 일치할 경우에만 삭제가 수행됩니다.
     * 
     * @param alarmId  삭제할 알람의 ID
     * @param username 삭제를 요청한 사용자 계정명
     */
    @Transactional
    public void deleteAlarm(Long alarmId, String username) {
        alarmRepository.findById(alarmId).ifPresent(alarm -> {
            if (alarm.getUser().getUsername().equals(username)) {
                alarmRepository.delete(alarm);
            }
        });
    }

    /**
     * 백그라운드 주기적 알람 발송 스케줄러.
     * 30초 주기로 실행되며, 현재 시간 이전으로 설정된 미발송 알람들을 찾아 발송을 트리거합니다.
     * 매일 반복 알람인 경우 발송 후 날짜를 하루 뒤로 연기합니다.
     */
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void triggerEmail() {
        LocalDateTime now = LocalDateTime.now();
        List<MedicineAlarm> pendingAlarms = alarmRepository.findByAlarmTimeBeforeAndIsSentFalse(now);

        for (MedicineAlarm alarm : pendingAlarms) {
            boolean success = sendEmail(alarm);
            if (success) {
                if (alarm.isDaily()) {
                    alarm.setAlarmTime(alarm.getAlarmTime().plusDays(1));
                    log.info("Daily alarm rescheduled for user {} to {}", alarm.getUser().getUsername(),
                            alarm.getAlarmTime());
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

    /**
     * 외부 스크립트(Google Apps Script)를 호출하여 실제 이메일을 발송합니다.
     * 포트 차단 등 호스팅 서버의 SMTP 제한을 우회하기 위해 HTTP POST 방식을 사용합니다.
     * 
     * @param alarm 발송할 대상 알람 엔티티
     * @return 이메일 발송 성공 여부
     */
    private boolean sendEmail(MedicineAlarm alarm) {
        User user = alarm.getUser();
        try {
            String content = user.getUsername() + " 님 설정하신 약의 복용시간입니다!" + (alarm.isDaily() ? " (매일 반복)" : "");

            Map<String, String> payload = new HashMap<>();
            payload.put("to", user.getEmail());
            payload.put("subject", "[Medicine Helper] 약 복용 시간 알림 💊");
            payload.put("body", content);

            restTemplate.postForEntity(scriptUrl, payload, String.class);
            return true;
        } catch (Exception e) {
            log.error("Error sending email: ", e);
            return false;
        }
    }
}
