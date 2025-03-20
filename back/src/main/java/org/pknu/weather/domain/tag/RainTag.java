package org.pknu.weather.domain.tag;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.exception.GeneralException;

@Getter
@RequiredArgsConstructor

public enum RainTag implements EnumTag {
    NOTHING("", "강수 없음", 1),
    LIGHT_RAIN("", "약한 비", 2),
    MODERATE_RAIN("", "보통 비", 3),
    HEAVY_RAIN("", "강한 비", 4),
    EXTREME_RAIN("", "매우 강한 비", 5),

    LIGHT_RAIN_AND_SNOW("", "약한 눈/비", 6),
    MODERATE_RAIN_AND_SNOW("", "보통 눈/비", 7),
    HEAVY_RAIN_AND_SNOW("", "강한 눈/비", 8),
    EXTREME_RAIN_AND_SNOW("매우", "강한 눈/비", 9),

    LIGHT_SNOW("", "약한 눈", 10),
    MODERATE_SNOW("", "보통 눈", 11),
    HEAVY_SNOW("", "강한 눈", 12),

    SHOWER("", "소나기", 20),
    ;

    private final String adverb;
    private final String text;
    private final Integer code;

    @Override
    public EnumTag findByCode(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findAny()
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public EnumTag weatherValueToTag(Weather weather) {
        return NOTHING;
    }
}
