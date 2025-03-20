package org.pknu.weather.domain.tag;

import java.util.Optional;
import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Weather;

public interface EnumTag {
    EnumTag findByCode(int code);

    String getKey();

    String getText();

    Integer getCode();

    default String toText() {
        return (getAdverb() + " " + getText()).trim();
    }

    default String getAdverb() {
        return "";
    }

    ;

    default String getTagName() {
        String[] split = getClass().toString().split("\\.");
        return split[split.length - 1];
    }

    //    EnumTag weatherValueToTag(Weather weather, Optional<ExtraWeather> extraWeatherOptional);
    EnumTag weatherValueToTag(Weather weather);

    default Boolean tagSelectedCheck(EnumTag tag, Weather weather, Optional<ExtraWeather> extraWeatherOptional) {
        EnumTag enumTag = tag.weatherValueToTag(weather);
        return enumTag == tag;

//        if (tag instanceof WindTag) {
//            return tag == WindTag.windSpeedToWindTag(weather.getWindSpeed());
//        } else if (tag instanceof HumidityTag) {
//            return tag == HumidityTag.humidityToHumidityTag(weather.getHumidity());
//        } else if (tag instanceof SkyTag) {
//            return tag == SkyTag.CLEAR;
//        } else if (tag instanceof DustTag) {
//            return extraWeatherOptional.map(extraWeather -> tag == DustTag.pmValueToDustTag(extraWeather))
//                    .orElseGet(() -> tag == DustTag.GOOD);
//        }
//        return false;
    }
}
