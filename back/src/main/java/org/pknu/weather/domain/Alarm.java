package org.pknu.weather.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.pknu.weather.domain.common.SummaryAlarmTime;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "alarm_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    @Column
    private String fcmToken;

    @ColumnDefault("false")
    private Boolean agreeTempAlarm; // 온도 알람

    @ColumnDefault("false")
    private Boolean agreePrecipAlarm; // 강수 알람

    @ColumnDefault("false")
    private Boolean agreeDustAlarm; // 미세먼지 알람

    @ColumnDefault("false")
    private Boolean agreeUvAlarm; // 자외선 알람

    @ColumnDefault("false")
    private Boolean agreeLiveRainAlarm; // 실시간 비 알람

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "alarm_summary_time", joinColumns = @JoinColumn(name = "alarm_id"))
    @Column(name = "summary_time")
    private Set<SummaryAlarmTime> summaryAlarmTimes;
}
