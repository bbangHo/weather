package org.pknu.weather.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.apiPayload.code.BaseCode;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.Alarm;
import org.pknu.weather.domain.common.SummaryAlarmTime;
import org.pknu.weather.dto.AlarmRequestDTO;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.repository.AlarmRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlarmService 유닛 테스트") // 테스트 이름 변경
class AlarmServiceTest {

    @InjectMocks
    private AlarmService alarmService;

    @Mock
    private AlarmRepository alarmRepository;

    private MockedStatic<Alarm> mockedAlarmStatic;

    @BeforeEach
    void setUp() {
        mockedAlarmStatic = Mockito.mockStatic(Alarm.class);
    }

    @AfterEach
    void tearDown() {
        mockedAlarmStatic.close();
    }


    @Test
    @DisplayName("modifyAlarm 성공: 알람 정보를 정상적으로 수정하고 저장한다")
    void modifyAlarm_Success() {

        mockedAlarmStatic.when(Alarm::builder).thenCallRealMethod();

        // Given
        AlarmRequestDTO requestDTO = AlarmRequestDTO.builder()
                .fcmToken("valid_fcm_token")
                .agreeTempAlarm(true)
                .agreePrecipAlarm(false)
                .agreeDustAlarm(true)
                .agreeUvAlarm(true)
                .agreeLiveRainAlarm(false)
                .summaryAlarmTimes(new HashSet<>(Set.of(SummaryAlarmTime.MORNING, SummaryAlarmTime.EVENING)))
                .build();

        Alarm foundAlarm = Alarm.builder()
                .id(1L)
                .fcmToken("valid_fcm_token")
                .agreeTempAlarm(false)
                .agreePrecipAlarm(true)
                .agreeDustAlarm(false)
                .agreeUvAlarm(false)
                .agreeLiveRainAlarm(true)
                .summaryAlarmTimes(new HashSet<>(Set.of(SummaryAlarmTime.AFTERNOON)))
                .build();

        Alarm expectedModifiedAlarm = Alarm.builder()
                .id(1L)
                .fcmToken("valid_fcm_token")
                .agreeTempAlarm(true)
                .agreePrecipAlarm(false)
                .agreeDustAlarm(true)
                .agreeUvAlarm(true)
                .agreeLiveRainAlarm(false)
                .summaryAlarmTimes(new HashSet<>(Set.of(SummaryAlarmTime.MORNING, SummaryAlarmTime.EVENING)))
                .build();

        // Mocking
        when(alarmRepository.findByFcmToken(requestDTO.getFcmToken())).thenReturn(Optional.of(foundAlarm));
        mockedAlarmStatic.when(() -> Alarm.modifyAlarm(eq(foundAlarm), eq(requestDTO))).thenReturn(expectedModifiedAlarm);
        when(alarmRepository.saveAndFlush(any(Alarm.class))).thenReturn(expectedModifiedAlarm);

        alarmService.modifyAlarm(requestDTO);


        // Then
        verify(alarmRepository).findByFcmToken(requestDTO.getFcmToken());
        mockedAlarmStatic.verify(() -> Alarm.modifyAlarm(eq(foundAlarm), eq(requestDTO)));
        verify(alarmRepository).saveAndFlush(eq(expectedModifiedAlarm));
        verify(alarmRepository, times(1)).saveAndFlush(any(Alarm.class));
    }

    @Test
    @DisplayName("modifyAlarm 실패: 해당 fcmToken의 알람을 찾을 수 없을 때 GeneralException 발생")
    void modifyAlarm_NotFound_ThrowsException() {
        // Given
        AlarmRequestDTO requestDTO = AlarmRequestDTO.builder()
                .fcmToken("non_existent_fcm_token")
                .build();

        // Mocking
        when(alarmRepository.findByFcmToken(requestDTO.getFcmToken())).thenReturn(Optional.empty());

        // When & Then
        // AssertJ의 assertThatThrownBy를 사용하여 예외 발생 및 예외 내용을 검증합니다.
        assertThatThrownBy(() -> {
            alarmService.modifyAlarm(requestDTO);
        })
                .isInstanceOf(GeneralException.class)
                .extracting("code") // 방법 2 (권장): getCode() 메서드 참조로 추출
                .isEqualTo(ErrorStatus._FCMTOKEN_NOT_FOUND);


        // Mockito 검증 (동일)
        verify(alarmRepository).findByFcmToken(requestDTO.getFcmToken());
        mockedAlarmStatic.verifyNoInteractions(); // 정적 메서드 호출 없음 검증
        verify(alarmRepository, never()).saveAndFlush(any(Alarm.class)); // saveAndFlush 호출 없음 검증
    }
}