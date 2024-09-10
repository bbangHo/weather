package org.pknu.weather.domain.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum HumidityTag implements EnumTag {
    DRY("", "건조함", 1),
    COMMON_HUMID("", "보통", 2),
    LITTLE_HUMID("약간", "습함", 3),
    HUMID("", "습함", 4),
    VERY_HUMID("매우", "습함", 5)
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
