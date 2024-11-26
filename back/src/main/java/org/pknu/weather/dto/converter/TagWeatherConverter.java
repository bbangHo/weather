package org.pknu.weather.dto.converter;

import org.pknu.weather.domain.Tag;
import org.pknu.weather.domain.TagWeather;
import org.pknu.weather.domain.Weather;

public class TagWeatherConverter {

    public static TagWeather toTagWeather(Tag tag, Weather weather) {
        return TagWeather.builder()
                .tag(tag)
                .weather(weather)
                .build();
    }
}
