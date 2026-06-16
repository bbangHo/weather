package org.pknu.weather.weather.event;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.service.WeatherRefreshAsyncService;

@ExtendWith(MockitoExtension.class)
class WeatherRefreshListenerTest {

    @Mock
    private WeatherRefreshAsyncService weatherRefreshAsyncService;

    @InjectMocks
    private WeatherRefreshListener weatherRefreshListener;

    @Test
    void handleCreateEvent_withoutForecast_delegatesCreateByLocationId() {
        Long locationId = 1L;

        weatherRefreshListener.handle(new WeatherCreateEvent(locationId));

        verify(weatherRefreshAsyncService).createAndCache(locationId);
        verifyNoMoreInteractions(weatherRefreshAsyncService);
    }

    @Test
    void handleCreateEvent_withForecast_delegatesCreateWithForecast() {
        Long locationId = 1L;
        List<Weather> forecast = List.of(Weather.builder().build());

        weatherRefreshListener.handle(new WeatherCreateEvent(locationId, forecast));

        verify(weatherRefreshAsyncService).createAndCache(locationId, forecast);
        verifyNoMoreInteractions(weatherRefreshAsyncService);
    }

    @Test
    void handleUpdateEvent_withoutForecast_delegatesUpdateByLocationId() {
        Long locationId = 1L;

        weatherRefreshListener.handle(new WeatherUpdateEvent(locationId));

        verify(weatherRefreshAsyncService).updateAndCache(locationId);
        verifyNoMoreInteractions(weatherRefreshAsyncService);
    }

    @Test
    void handleUpdateEvent_withForecast_delegatesUpdateWithForecast() {
        Long locationId = 1L;
        List<Weather> forecast = List.of(Weather.builder().build());

        weatherRefreshListener.handle(new WeatherUpdateEvent(locationId, forecast));

        verify(weatherRefreshAsyncService).updateAndCache(locationId, forecast);
        verifyNoMoreInteractions(weatherRefreshAsyncService);
    }

    @Test
    void handleCacheRefreshEvent_withoutForecast_refreshesCacheFromDb() {
        Long locationId = 1L;

        weatherRefreshListener.handle(new WeatherCacheRefreshEvent(locationId));

        verify(weatherRefreshAsyncService).refreshCacheFromDb(locationId);
        verifyNoMoreInteractions(weatherRefreshAsyncService);
    }

    @Test
    void handleCacheRefreshEvent_withForecast_refreshesCacheFromForecast() {
        Long locationId = 1L;
        List<Weather> forecast = List.of(Weather.builder().build());

        weatherRefreshListener.handle(new WeatherCacheRefreshEvent(locationId, forecast));

        verify(weatherRefreshAsyncService).refreshCacheFromForecast(locationId, forecast);
        verifyNoMoreInteractions(weatherRefreshAsyncService);
    }
}
