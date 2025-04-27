package org.pknu.weather.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Clock;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.QAlarm;
import org.pknu.weather.domain.QMember;
import org.pknu.weather.domain.common.SummaryAlarmTime;
import org.pknu.weather.dto.AlarmMemberDTO;

@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final Clock clock;


    public List<AlarmMemberDTO> findMembersAndAlarmsByAlarmType() {
        return findMembersAndAlarmsByAlarmTime(getCurrentAlarmTime());
    }

    private SummaryAlarmTime getCurrentAlarmTime() {
        LocalTime now = LocalTime.now(clock);
        if (now.isBefore(LocalTime.of(10, 0))) {
            return SummaryAlarmTime.MORNING;
        } else if (now.isBefore(LocalTime.of(15, 0))) {
            return SummaryAlarmTime.AFTERNOON;
        } else {
            return SummaryAlarmTime.EVENING;
        }
    }

    private List<AlarmMemberDTO> findMembersAndAlarmsByAlarmTime(SummaryAlarmTime time) {
        QAlarm alarm = QAlarm.alarm;
        QMember member = QMember.member;

        return queryFactory
                .select(Projections.fields(AlarmMemberDTO.class,
                        member.id,
                        member.location.id.as("locationId"),
                        alarm.fcmToken,
                        alarm.agreeTempAlarm,
                        alarm.agreePrecipAlarm,
                        alarm.agreeDustAlarm,
                        alarm.agreeUvAlarm,
                        alarm.agreeLiveRainAlarm
                ))
                .from(alarm)
                .join(alarm.member, member)
                .where(alarm.summaryAlarmTimes.contains(time))
                .fetch();
    }
}
