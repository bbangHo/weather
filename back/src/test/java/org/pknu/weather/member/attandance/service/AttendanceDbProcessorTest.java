package org.pknu.weather.member.attandance.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.member.attandance.entity.Attendance;
import org.pknu.weather.member.attandance.repository.AttendanceRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.event.AttendanceCheckedEvent;
import org.pknu.weather.member.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceDbProcessorTest {

    @InjectMocks
    private AttendanceDbProcessor attendanceDbProcessor;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("DB 프로세서: 멤버를 조회하고 출석 데이터를 저장한 뒤 이벤트를 발행한다")
    void processCheckInDbLogic_Success() {
        // given
        String testEmail = "tester@test.com";
        Member mockMember = mock(Member.class);
        when(mockMember.getEmail()).thenReturn(testEmail);
        when(memberRepository.safeFindByEmail(testEmail)).thenReturn(mockMember);

        // when
        attendanceDbProcessor.processCheckInDbLogic(testEmail);

        // then
        // 1. 멤버 조회가 일어났는지 검증
        verify(memberRepository, times(1)).safeFindByEmail(testEmail);

        // 2. 출석 데이터가 save 되었는지 검증
        verify(attendanceRepository, times(1)).save(any(Attendance.class));

        // 3. 출석 완료 이벤트가 정상적으로 발행되었는지 검증
        verify(eventPublisher, times(1)).publishEvent(any(AttendanceCheckedEvent.class));
    }
}