package org.pknu.weather.domain.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DustTag implements EnumTag {
    VERY_GOOD("매우", "좋음", 1),
    GOOD("", "좋음", 2),
    NORMAL("", "보통", 3),
    LITTLE_BAD("약간", "나쁨", 4),
    VERY_BAD("매우", "나쁨", 5)
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
}

