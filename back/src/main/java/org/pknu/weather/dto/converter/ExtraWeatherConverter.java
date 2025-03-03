package org.pknu.weather.dto.converter;

import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Location;
import org.pknu.weather.dto.WeatherResponse;

public class ExtraWeatherConverter {

    public static WeatherResponse.ExtraWeatherInfo toExtraWeatherInfo(ExtraWeather extraWeather) {
        return WeatherResponse.ExtraWeatherInfo.builder()
                .baseTime(extraWeather.getBasetime())
                .uvGrade(extraWeather.getUv())
                .uvGradePlus3(extraWeather.getUvPlus3())
                .uvGradePlus6(extraWeather.getUvPlus6())
                .uvGradePlus9(extraWeather.getUvPlus9())
                .uvGradePlus12(extraWeather.getUvPlus12())
                .uvGradePlus15(extraWeather.getUvPlus15())
                .uvGradePlus18(extraWeather.getUvPlus18())
                .uvGradePlus21(extraWeather.getUvPlus21())
                .o3Grade(extraWeather.getO3())
                .pm10Grade(extraWeather.getPm10())
                .pm10Value(extraWeather.getPm10value())
                .pm25Grade(extraWeather.getPm25())
                .pm25Value(extraWeather.getPm25value())
                .build();
    }

    public static ExtraWeather toExtraWeather(Location location, WeatherResponse.ExtraWeatherInfo extraWeatherInfo) {
        return ExtraWeather.builder()
                .location(location)
                .basetime(extraWeatherInfo.getBaseTime())
                .uv(extraWeatherInfo.getUvGrade())
                .uvPlus3(extraWeatherInfo.getUvGradePlus3())
                .uvPlus6(extraWeatherInfo.getUvGradePlus6())
                .uvPlus9(extraWeatherInfo.getUvGradePlus9())
                .uvPlus12(extraWeatherInfo.getUvGradePlus12())
                .uvPlus15(extraWeatherInfo.getUvGradePlus15())
                .uvPlus18(extraWeatherInfo.getUvGradePlus18())
                .uvPlus21(extraWeatherInfo.getUvGradePlus21())
                .o3(extraWeatherInfo.getO3Grade())
                .pm10(extraWeatherInfo.getPm10Grade())
                .pm10value(extraWeatherInfo.getPm10Value())
                .pm25(extraWeatherInfo.getPm25Grade())
                .pm25value(extraWeatherInfo.getPm25Value())
                .build();
    }
}
