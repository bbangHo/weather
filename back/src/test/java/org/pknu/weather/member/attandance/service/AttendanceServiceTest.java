package org.pknu.weather.member.attandance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @InjectMocks
    private AttendanceService attendanceService;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private AttendanceDbProcessor attendanceDbProcessor;

    private final String testEmail = "tester@test.com";
    private String expectedRedisKey;

    @BeforeEach
    void setUp() {
        expectedRedisKey = "attendance:" + LocalDate.now() + ":" + testEmail;
        // opsForValue() 호출 시 Mock 객체 반환 설정
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("최초 출석 시도: Redis에 값이 없으면 DB 프로세서를 정상 호출한다")
    void checkInV2_FirstTime_Success() {
        // given
        when(valueOperations.setIfAbsent(eq(expectedRedisKey), eq("Y"), any(Duration.class)))
                .thenReturn(true);

        // when
        attendanceService.checkInV2(testEmail);

        // then
        verify(attendanceDbProcessor, times(1)).processCheckInDbLogic(testEmail, LocalDate.now());
        verify(stringRedisTemplate, never()).delete(expectedRedisKey);
    }

    @Test
    @DisplayName("중복 출석 시도: Redis에 이미 값이 있으면 DB 프로세서를 호출하지 않고 종료한다 (Fast-Fail)")
    void checkInV2_AlreadyCheckedIn_FastFail() {
        // given
        when(valueOperations.setIfAbsent(eq(expectedRedisKey), eq("Y"), any(Duration.class)))
                .thenReturn(false);

        // when
        assertThrows(GeneralException.class, () -> {
            attendanceService.checkInV2(testEmail);
        });

        // then
        // 핵심 검증: DB 로직이 절대 호출되지 않아야 함
        verify(attendanceDbProcessor, never()).processCheckInDbLogic(anyString(), any(LocalDate.class));
    }

    @Test
    @DisplayName("DB 프로세스 실패: DB 저장 중 예외 발생 시 Redis 키를 삭제하여 롤백한다")
    void checkInV2_DbProcessFails_RollbackRedis() {
        // given
        when(valueOperations.setIfAbsent(eq(expectedRedisKey), eq("Y"), any(Duration.class)))
                .thenReturn(true);

        // DB 로직에서 런타임 예외 발생 시뮬레이션
        doThrow(new RuntimeException("DB Connection Error"))
                .when(attendanceDbProcessor).processCheckInDbLogic(testEmail, LocalDate.now());

        // when & then
        assertThrows(RuntimeException.class, () -> attendanceService.checkInV2(testEmail));

        // 예외가 발생했으므로 Redis에서 키를 삭제하는 보상 로직이 실행되어야 함
        verify(stringRedisTemplate, times(1)).delete(expectedRedisKey);
    }

    @Test
    @DisplayName("DB 프로세스 실패: redis에는 값이 없지만 DB에 값이 있을 경우, 비즈니스 예외를 발생시킵니다.")
    void checkInV2_DbProcessFails_DuplicateData() {
        // given
        when(valueOperations.setIfAbsent(eq(expectedRedisKey), eq("Y"), any(Duration.class)))
                .thenReturn(true);

        // DB 로직에서 런타임 예외 발생 시뮬레이션
        doThrow(new DataIntegrityViolationException("데이터 중복 발생"))
                .when(attendanceDbProcessor).processCheckInDbLogic(testEmail, LocalDate.now());

        // when & then
        assertThrows(GeneralException.class, () -> attendanceService.checkInV2(testEmail));

        // 예외가 발생했으므로 Redis에서 키를 삭제하는 보상 로직이 실행되어야 함
        verify(stringRedisTemplate, never()).delete(expectedRedisKey);
    }
}