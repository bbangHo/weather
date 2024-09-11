package org.pknu.weather.dto.converter;

import org.pknu.weather.common.utils.TagUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.tag.RainTag;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.dto.TagDto;
import org.pknu.weather.dto.WeatherResponse;

import java.util.List;

public class WeatherConverter {

    public static WeatherResponse.MainPageWeatherData toMainPageWeatherData(List<Weather> weatherList, Member member) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for(Weather w : weatherList) {
            max = Math.max(max, w.getTemperature());
            min = Math.min(min, w.getTemperature());
        }

        List<WeatherResponse.WeatherPerHour> weatherPerHourList = weatherList.stream()
                .map(weather -> toWeatherPerHour(weather, member))
                .toList();

        Location location = weatherList.get(0).getLocation();
        Weather now = weatherList.get(0);

        return WeatherResponse.MainPageWeatherData.builder()
                .location(location.getAddress())
                .currentSkyType(now.getSkyType())
                .currentTmp(now.getTemperature())
                .weatherPerHourList(weatherPerHourList)
                .temperature(toTemperature(max, min))
                .build();
    }

    public static WeatherResponse.WeatherPerHour toWeatherPerHour(Weather weather, Member member) {
        RainTag rainTag = TagUtils.rain2Text(weather.getRain());
        TemperatureTag temperatureTag = TagUtils.tmp2Text(
                weather.getTemperature(), member.getSensitivity());

        return WeatherResponse.WeatherPerHour.builder()
                .hour(weather.getPresentationTime())
                .skyType(weather.getSkyType())
                .rainAdverb(rainTag.getAdverb())
                .rainText(rainTag.getText())
                .rain(weather.getRain())
                .tmpAdverb(temperatureTag.getAdverb())
                .tmpText(temperatureTag.getText())
                .tmp(weather.getTemperature())
                .build();
    }

    public static WeatherResponse.Temperature toTemperature(Integer max, Integer min) {
        return WeatherResponse.Temperature.builder()
                .maxTmp(max)
                .minTmp(min)
                .build();
    }

    public static WeatherResponse.WeatherSimpleInfo toSimpleWeatherInfo(List<TagDto.SimpleTag> tagList) {
        return WeatherResponse.WeatherSimpleInfo.builder()
                .tags(tagList.stream().map(TagDto.SimpleTag::getText).toList())
                .prcpProb(null)
                .days(null)
                .build();
    }
}
