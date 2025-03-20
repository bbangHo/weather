package org.pknu.weather.domain.tag;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.dto.TotalWeatherDto;
import org.pknu.weather.exception.GeneralException;

@Getter
@RequiredArgsConstructor
public enum HumidityTag implements EnumTag {
    DRY("", "건조함", 1),
    PLEASANT("", "쾌적함", 2),
    LITTLE_HUMID("약간", "습함", 3),
    HUMID("", "습함", 4),
    VERY_HUMID("매우", "습함", 5);

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
    public EnumTag weatherValueToTag(TotalWeatherDto totalWeatherDto) {
        Integer humidity = totalWeatherDto.getWeatherDto().getHumidity();
        if (humidity < 30) {
            return HumidityTag.DRY;
        } else if (humidity < 37) {
            return HumidityTag.PLEASANT;
        } else if (humidity < 44) {
            return HumidityTag.LITTLE_HUMID;
        } else if (humidity < 52) {
            return HumidityTag.HUMID;
        } else {
            return HumidityTag.VERY_HUMID;
        }
    }
}
