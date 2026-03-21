package org.pknu.weather.member.attandance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.apipayload.code.status.SuccessStatus;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.member.attandance.entity.Attendance;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.event.AttendanceCheckedEvent;
import org.pknu.weather.member.attandance.repository.AttendanceRepository;
import org.pknu.weather.member.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AttendanceRepository attendanceRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final AttendanceDbProcessor attendanceDbProcessor;

    public void checkInV2(String email) {
        String redisKey = "attendance:" + LocalDate.now() + ":" + email;

        // 1. Redis에서 오늘 출석 여부 원자적(Atomic) 확인 및 세팅 (00~24시까지 TTL 설정)
        Boolean isFirstCheckIn = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, "Y", Duration.between(LocalDateTime.now(), LocalDateTime.now().with(LocalTime.MAX)));

        // 2. 이미 출석한 경우 DB를 찌르지 않고 즉시 반환 (Fast-Return)
        if (Boolean.FALSE.equals(isFirstCheckIn)) {
            throw new GeneralException(ErrorStatus._ALREADY_ATTENDED);
        }

        try {
            attendanceDbProcessor.processCheckInDbLogic(email);
        } catch (Exception e) {
            // DB 저장 실패 시 Redis 캐시 롤백 (보상 트랜잭션)
            stringRedisTemplate.delete(redisKey);
            throw e;
        }
    }
}
