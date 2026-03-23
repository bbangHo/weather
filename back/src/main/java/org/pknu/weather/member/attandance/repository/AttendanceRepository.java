package org.pknu.weather.member.attandance.repository;

import org.pknu.weather.member.attandance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Boolean existsByMemberIdAndDate(Long member_id, LocalDate date);
}
