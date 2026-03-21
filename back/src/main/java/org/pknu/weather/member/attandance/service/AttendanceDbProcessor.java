package org.pknu.weather.member.attandance.service;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.member.attandance.entity.Attendance;
import org.pknu.weather.member.attandance.repository.AttendanceRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.event.AttendanceCheckedEvent;
import org.pknu.weather.member.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AttendanceDbProcessor {

    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void processCheckInDbLogic(String email) {
        Member member = memberRepository.safeFindByEmail(email);

        Attendance attendance = Attendance.builder()
                .date(LocalDate.now())
                .member(member)
                .build();

        attendance.checkIn();
        attendanceRepository.save(attendance);

        eventPublisher.publishEvent(new AttendanceCheckedEvent(member.getEmail()));
    }
}
