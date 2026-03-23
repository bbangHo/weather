package org.pknu.weather.member.attandance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.member.attandance.entity.Attendance;
import org.pknu.weather.member.attandance.repository.AttendanceRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.event.AttendanceCheckedEvent;
import org.pknu.weather.member.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceDbProcessor {

    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void processCheckInDbLogic(String email, LocalDate date) {
        Member member = memberRepository.safeFindByEmail(email);

        try {
            // 기존의 중복 검사 및 INSERT 로직
            if (!attendanceRepository.existsByMemberIdAndDate(member.getId(), date)) {
                Attendance attendance = Attendance.builder()
                        .date(LocalDate.now())
                        .member(member)
                        .build();

                attendanceRepository.save(attendance);
            }
        } catch (DataIntegrityViolationException e) {
            // 이미 다른 스레드에서 INSERT를 성공해서 유니크 제약조건에 걸린 경우
            // 에러를 던지지 않고, 로그만 남기거나 정상(이미 출석됨)으로 처리
            log.warn("동시성 이슈로 인한 중복 출석체크 - memberId: {}", member.getId());
            // throw new CustomException(ErrorCode.ALREADY_CHECKED_IN); // 필요시 커스텀 예외로 변환
        }

        eventPublisher.publishEvent(new AttendanceCheckedEvent(member.getEmail()));
    }
}
