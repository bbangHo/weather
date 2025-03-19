package org.pknu.weather.domain.tag;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;

@Getter
@RequiredArgsConstructor
public enum DustTag implements EnumTag {
    GOOD("", "좋음", 1),
    NORMAL("", "보통", 2),
    LITTLE_BAD("", "나쁨", 3),
    VERY_BAD("매우", "나쁨", 4);

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

