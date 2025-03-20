package org.pknu.weather.dto.converter;

import java.util.Optional;
import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.dto.TagDto;
import org.pknu.weather.dto.TagSelectedOrNotDto;

public class TagResponseConverter {

    public static TagDto.SimpleTag toSimpleTag(String text) {
        return TagDto.SimpleTag.builder()
                .text(text)
                .build();
    }

    public static TagSelectedOrNotDto toTagSelectedOrNotDto(EnumTag enumTag, Weather weather,
                                                            Optional<ExtraWeather> extraWeatherOptional) {
        return TagSelectedOrNotDto.builder()
                .enumTag(enumTag)
                .selected(enumTag.tagSelectedCheck(enumTag, weather, extraWeatherOptional))
                .build();
    }

}
