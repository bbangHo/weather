package org.pknu.weather.service.supports;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public enum AlarmType {
    WEATHER_SUMMARY("날씨 요약 알림"),
    RAIN_ALERT("실시간 비 알림"),
    WEATHER_UPDATE("날씨 데이터 업데이트");

    private final String description;
}
