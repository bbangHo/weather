package org.pknu.weather.weather.event;

import lombok.AllArgsConstructor;
import org.pknu.weather.weather.service.WeatherRefreshAsyncService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class WeatherRefreshListener {
    private final WeatherRefreshAsyncService weatherRefreshAsyncService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handle(WeatherCreateEvent event) {
        if (event.getNewForecast() == null) {
            weatherRefreshAsyncService.createAndCache(event.getLocationId());
            return;
        }
        weatherRefreshAsyncService.createAndCache(event.getLocationId(), event.getNewForecast());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handle(WeatherUpdateEvent event) {
        if (event.getNewForecast() == null) {
            weatherRefreshAsyncService.updateAndCache(event.getLocationId());
            return;
        }
        weatherRefreshAsyncService.updateAndCache(event.getLocationId(), event.getNewForecast());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handle(WeatherCacheRefreshEvent event) {
        if (event.getForecast() == null) {
            weatherRefreshAsyncService.refreshCacheFromDb(event.getLocationId());
            return;
        }
        weatherRefreshAsyncService.refreshCacheFromForecast(event.getLocationId(), event.getForecast());
    }
}
