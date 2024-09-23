package org.pknu.weather.feignClient.utils;

import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherApiResponse.Response.Body.Items.Item;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherApiUtils {

    /**
     * 단기 날씨 예보 API 에서 얻은 데이터를 Weather 데이터로 가공하는 메서드
     * @param itemList 단기 날씨 예보 API 에서 얻은 데이터
     * @param date ex. "202409"
     * @param time ex. "0500"
     * @return
     */
    public static List<Weather> responseProcess(List<Item> itemList, String date, String time) {
        Map<String, Weather> weatherMap = new HashMap<>();
        LocalDateTime baseTime = DateTimeFormatter.formattedDateTime2LocalDateTime(date, time);

        for (Item item : itemList) {
            String fcstTime = item.getFcstTime();
            LocalDateTime presentationTime = DateTimeFormatter.formattedDateTime2LocalDateTime(date, fcstTime);

            if(presentationTime.isBefore(baseTime)) {
                presentationTime = presentationTime.plusDays(1L);
            }

            if (!weatherMap.containsKey(fcstTime)) {
                Weather weather = Weather.builder()
                        .basetime(baseTime)
                        .presentationTime(presentationTime)
                        .build();

                weatherMap.put(fcstTime, weather);
            }

            Weather weather = weatherMap.get(fcstTime);
            weather.categoryClassify(item);
            weatherMap.put(fcstTime, weather);
        }

        return weatherMap.values().stream()
                .filter(weather -> weather.getPresentationTime().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Weather::getPresentationTime))
                .toList();
    }
}
