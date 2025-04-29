package org.pknu.weather.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.domain.Alarm;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.common.SummaryAlarmTime;
import org.pknu.weather.dto.AlarmMemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@Import(DataJpaTestConfig.class)
@DataJpaTest
class MemberCustomRepositoryImplTest {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private TestEntityManager testEntityManager;

    @ParameterizedTest
    @MethodSource("provideMatchedSummaryAlarmTimes")
    void 시간대에_맞는_알람만_조회된다(LocalTime fixedTime, Set<SummaryAlarmTime> expectedAlarmTimes) {

        // Given
        Clock clock = createFixedClock(fixedTime);
        saveMemberAndAlarm("test@example.com", "test-fcm-token", expectedAlarmTimes);

        MemberCustomRepositoryImpl repository = createRepository(clock);

        // When
        List<AlarmMemberDTO> result = repository.findMembersAndAlarmsByAlarmType();

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting(AlarmMemberDTO::getFcmToken)
                .containsExactly("test-fcm-token");
    }

    @ParameterizedTest
    @MethodSource("provideMismatchedSummaryAlarmTimes")
    void 시간대가_다르면_알람이_조회되지_않는다(LocalTime fixedTime, Set<SummaryAlarmTime> alarmSummaryTimes) {
        // Given
        Clock clock = createFixedClock(fixedTime);
        saveMemberAndAlarm("mismatch@example.com", "mismatch-fcm-token", alarmSummaryTimes);

        MemberCustomRepositoryImpl repository = createRepository(clock);

        // When
        List<AlarmMemberDTO> result = repository.findMembersAndAlarmsByAlarmType();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void 같은_시간대에_여러명의_알람이_조회된다() {

        // Given
        Clock clock = createFixedClock(LocalTime.of(7, 0));

        saveMemberAndAlarm("user1@example.com", "fcm-token-1", Collections.singleton(SummaryAlarmTime.MORNING));
        saveMemberAndAlarm("user2@example.com", "fcm-token-2", Set.of(SummaryAlarmTime.MORNING,SummaryAlarmTime.AFTERNOON));
        saveMemberAndAlarm("user3@example.com", "fcm-token-3", Collections.singleton(SummaryAlarmTime.MORNING));

        MemberCustomRepositoryImpl repository = createRepository(clock);

        // When
        List<AlarmMemberDTO> result = repository.findMembersAndAlarmsByAlarmType();

        // Then
        assertThat(result)
                .hasSize(3)
                .extracting(AlarmMemberDTO::getFcmToken)
                .containsExactlyInAnyOrder("fcm-token-1", "fcm-token-2", "fcm-token-3");
    }

    private void saveMemberAndAlarm(String email, String fcmToken, Set<SummaryAlarmTime> alarmTime) {
        Member member = Member.builder()
                .email(email)
                .nickname("User " + email)
                .build();

        Alarm alarm = Alarm.builder()
                .member(member)
                .fcmToken(fcmToken)
                .agreeTempAlarm(true)
                .agreePrecipAlarm(true)
                .agreeDustAlarm(true)
                .agreeUvAlarm(true)
                .agreeLiveRainAlarm(true)
                .summaryAlarmTimes(alarmTime)
                .build();

        testEntityManager.persistAndFlush(member);
        testEntityManager.persistAndFlush(alarm);

    }

    private MemberCustomRepositoryImpl createRepository(Clock clock) {
        return new MemberCustomRepositoryImpl(jpaQueryFactory, clock);
    }

    private Clock createFixedClock(LocalTime time) {
        return Clock.fixed(
                time.atDate(LocalDate.now())
                        .atZone(ZoneId.systemDefault())
                        .toInstant(),
                ZoneId.systemDefault()
        );
    }

    static Stream<Arguments> provideMatchedSummaryAlarmTimes() {
        return Stream.of(
                arguments(LocalTime.of(7, 0), Set.of(SummaryAlarmTime.MORNING,SummaryAlarmTime.AFTERNOON)),
                arguments(LocalTime.of(12, 0), Set.of(SummaryAlarmTime.AFTERNOON)),
                arguments(LocalTime.of(18, 0), Set.of(SummaryAlarmTime.EVENING,SummaryAlarmTime.AFTERNOON))
        );
    }

    static Stream<Arguments> provideMismatchedSummaryAlarmTimes() {
        return Stream.of(
                arguments(LocalTime.of(7, 0), Set.of(SummaryAlarmTime.AFTERNOON,SummaryAlarmTime.EVENING)),
                arguments(LocalTime.of(12, 0),Set.of(SummaryAlarmTime.EVENING,SummaryAlarmTime.MORNING)),
                arguments(LocalTime.of(18, 0),Set.of(SummaryAlarmTime.MORNING))
        );
    }
}
