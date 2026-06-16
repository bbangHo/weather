package org.pknu.weather.weather.scheduler;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.weather.service.WeatherRefresherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherUpdateScheduler {
    private final WeatherRefresherService weatherRefresherService;

    @Value("${weather.update.limit-size:100}")
    private Integer updateLimitSize;

    @Value("${weather.update.dispatch-interval-millis:0}")
    private long dispatchIntervalMillis;

    /**
     * 02시 5분부터 3시간 간격으로 날씨 데이터를 업데이트합니다.
     */
    @Scheduled(cron = "0 5 2,5,8,11,14,17,20,23 * * *")
    public void executeWeatherUpdate () {
        weatherRefresherService.updateWeatherDataScheduled(updateLimitSize, dispatchIntervalMillis);
    }
}
