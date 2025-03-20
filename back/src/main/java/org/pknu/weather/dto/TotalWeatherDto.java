package org.pknu.weather.dto;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.common.RainType;
import org.pknu.weather.domain.common.SkyType;

@Getter
public class TotalWeatherDto {
    private final WeatherDto weatherDto;
    private final ExtraWeatherDto extraWeatherDto;
    private final LocalDateTime baseTime;

    public TotalWeatherDto(Weather weather) {
        baseTime = weather.getBasetime();
        weatherDto = WeatherDto.builder()
                .location(weather.getLocation())
                .windSpeed(weather.getWindSpeed())
                .humidity(weather.getHumidity())
                .rainProb(weather.getRainProb())
                .rain(weather.getRain())
                .rainType(weather.getRainType())
                .temperature(weather.getTemperature())
                .sensibleTemperature(weather.getSensibleTemperature())
                .snowCover(weather.getSnowCover())
                .skyType(weather.getSkyType())
                .presentationTime(weather.getPresentationTime())
                .build();
        extraWeatherDto = new ExtraWeatherDto();
    }

    public TotalWeatherDto(Weather weather, Optional<ExtraWeather> extraWeatherOptional) {
        baseTime = weather.getBasetime();
        weatherDto = WeatherDto.builder()
                .location(weather.getLocation())
                .windSpeed(weather.getWindSpeed())
                .humidity(weather.getHumidity())
                .rainProb(weather.getRainProb())
                .rain(weather.getRain())
                .rainType(weather.getRainType())
                .temperature(weather.getTemperature())
                .sensibleTemperature(weather.getSensibleTemperature())
                .snowCover(weather.getSnowCover())
                .skyType(weather.getSkyType())
                .presentationTime(weather.getPresentationTime())
                .build();
        ExtraWeather extraWeather = extraWeatherOptional.orElse(null);
        extraWeatherDto = ExtraWeatherDto.builder()
                .uv(extraWeather.getUv())
                .uvPlus3(extraWeather.getUvPlus3())
                .uvPlus6(extraWeather.getUvPlus6())
                .uvPlus9(extraWeather.getUvPlus9())
                .uvPlus12(extraWeather.getUvPlus12())
                .uvPlus15(extraWeather.getUvPlus15())
                .uvPlus18(extraWeather.getUvPlus18())
                .uvPlus21(extraWeather.getUvPlus21())
                .o3(extraWeather.getO3())
                .pm10(extraWeather.getPm10())
                .pm25(extraWeather.getPm25())
                .pm10value(extraWeather.getPm10value())
                .pm25value(extraWeather.getPm25value())
                .build();
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    @NoArgsConstructor
    public static class WeatherDto {
        private Location location;
        private Double windSpeed;
        private Integer humidity;
        private Integer rainProb;
        private Float rain;
        private RainType rainType;
        private Integer temperature;
        private Double sensibleTemperature;
        private Float snowCover;
        private SkyType skyType;
        private LocalDateTime presentationTime;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExtraWeatherDto {
        private Integer uv;
        private Integer uvPlus3;
        private Integer uvPlus6;
        private Integer uvPlus9;
        private Integer uvPlus12;
        private Integer uvPlus15;
        private Integer uvPlus18;
        private Integer uvPlus21;
        private Integer o3;
        private Integer pm10;
        private Integer pm25;
        private Integer pm10value;
        private Integer pm25value;
    }
}
