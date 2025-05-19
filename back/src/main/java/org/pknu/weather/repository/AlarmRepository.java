package org.pknu.weather.repository;


import java.util.Optional;
import org.pknu.weather.domain.Alarm;
import org.pknu.weather.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Optional<Alarm> findByFcmTokenAndMember(String fcmToken, Member member);

    @EntityGraph(attributePaths = {"summaryAlarmTimes"})
    @Query("SELECT a FROM Alarm a WHERE a.member = :member")
    Optional<Alarm> findByMemberWithSummaryAlarmTimes(@Param("member") Member member);

}
