package org.pknu.weather.common;

import org.pknu.weather.feignClient.dto.PointDTO;
import org.pknu.weather.dto.WeatherParams;

public class WeatherParamsFactory {
    public static WeatherParams create(String baseDate, String baseTime, PointDTO pointDTO) {
        return WeatherParams.builder()
                .base_date(baseDate)
                .base_time(baseTime)
                .nx(pointDTO.getX())
                .ny(pointDTO.getY())
                .build();
    }
}
