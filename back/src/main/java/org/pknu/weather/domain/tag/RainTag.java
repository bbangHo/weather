package org.pknu.weather.domain.tag;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;

@Getter
@RequiredArgsConstructor

public enum RainTag implements EnumTag {
    NOTHING("", "안옴", 1),
    ALMOST_NOTHING("거의", "안옴", 2),
    VERY_WEAK("매우", "약함", 3),
    WEAK("", "약함", 4),
    AVERAGE("", "보통", 5),
    STRONG("", "강함", 6),
    LITTLE_STRONG("조금", "강함", 7),
    VERY_STRONG("매우", "강함", 8);

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
