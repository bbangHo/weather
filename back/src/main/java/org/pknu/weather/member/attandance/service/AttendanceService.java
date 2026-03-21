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
import org.springframework.dao.DataIntegrityViolationException;
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

    private final StringRedisTemplate stringRedisTemplate;
    private final AttendanceDbProcessor attendanceDbProcessor;

    public void checkInV2(String email) {
        String redisKey = "attendance:" + LocalDate.now() + ":" + email;

        // Redis에서 오늘 출석 여부 원자적(Atomic) 확인 및 세팅 (00~24시까지 TTL 설정)
        // 키가 이미 존재해서 값을 세팅하지 못했다면 false를,키가 존재하지 않아서 값을 세팅했다면 true를 반환
        Boolean isFirstCheckIn = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, "Y", Duration.between(LocalDateTime.now(), LocalDateTime.now().with(LocalTime.MAX)));

        // 이미 출석한 경우 DB를 찌르지 않고 즉시 반환 (Fast-Return)
        if (Boolean.FALSE.equals(isFirstCheckIn)) {
            throw new GeneralException(ErrorStatus._ALREADY_ATTENDED);
        }

        try {
            attendanceDbProcessor.processCheckInDbLogic(email);
        } catch (DataIntegrityViolationException e) {
            // DB에 이미 데이터가 있음이 확인되었으므로, 다음 요청 방어를 위해 Redis 키를 유지해야 함 (delete 호출 안 함)
            throw new GeneralException(ErrorStatus._ALREADY_ATTENDED);
        } catch (Exception e) {
            // 이 경우에는 출석이 안 된 것이므로, 나중에 다시 시도할 수 있도록 Redis 키를 롤백함
            stringRedisTemplate.delete(redisKey);
            throw e;
        }
    }
}
