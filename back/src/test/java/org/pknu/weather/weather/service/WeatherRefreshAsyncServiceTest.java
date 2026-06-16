package org.pknu.weather.weather.service;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.enums.RainType;
import org.pknu.weather.weather.enums.SkyType;
import org.pknu.weather.weather.feignclient.weatherapi.target.WeatherApi;
import org.pknu.weather.weather.repository.WeatherRepository;

@ExtendWith(MockitoExtension.class)
class WeatherRefreshAsyncServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private WeatherCacheService weatherCacheService;

    @Mock
    private WeatherQueryService weatherQueryService;

    @Mock
    private WeatherApi weatherApi;

    @InjectMocks
    private WeatherRefreshAsyncService weatherRefreshAsyncService;

    @Test
    void createAndCache_fetchesApiThenSavesAndCaches() {
        Long locationId = 1L;
        var location = org.mockito.Mockito.mock(org.pknu.weather.location.entity.Location.class);
        List<Weather> forecast = List.of(Weather.builder().build());
        when(locationRepository.safeFindById(locationId)).thenReturn(location);
        when(weatherApi.getVillageShortTermForecast(location)).thenReturn(forecast);

        weatherRefreshAsyncService.createAndCache(locationId);

        InOrder inOrder = inOrder(locationRepository, weatherApi, weatherRepository, weatherCacheService);
        inOrder.verify(locationRepository).safeFindById(locationId);
        inOrder.verify(weatherApi).getVillageShortTermForecast(location);
        inOrder.verify(weatherRepository).batchUpsert(forecast, location);
        inOrder.verify(weatherCacheService).updateCachedWeathersForLocation(locationId, forecast);
        verify(weatherQueryService).markWeatherCreated(locationId);
    }

    @Test
    void updateAndCache_fetchesApiThenUpdatesAndCaches() {
        Long locationId = 1L;
        var location = org.mockito.Mockito.mock(org.pknu.weather.location.entity.Location.class);
        List<Weather> forecast = List.of(Weather.builder().build());
        when(locationRepository.safeFindById(locationId)).thenReturn(location);
        when(weatherApi.getVillageShortTermForecast(location)).thenReturn(forecast);
        when(weatherRepository.findAllByLocationAfterNow(location)).thenReturn(new java.util.HashMap<>());

        weatherRefreshAsyncService.updateAndCache(locationId);

        InOrder inOrder = inOrder(locationRepository, weatherApi, weatherRepository, weatherCacheService);
        inOrder.verify(locationRepository).safeFindById(locationId);
        inOrder.verify(weatherApi).getVillageShortTermForecast(location);
        inOrder.verify(weatherRepository).findAllByLocationAfterNow(location);
        inOrder.verify(weatherRepository).batchUpsert(forecast, location);
        inOrder.verify(weatherCacheService).updateCachedWeathersForLocation(locationId, forecast);
        verify(weatherQueryService).markWeatherUpdated(locationId);
    }

    @Test
    void updateAndCache_updatesExistingWeatherBeforeSavingAndCaching() {
        Long locationId = 1L;
        var location = org.mockito.Mockito.mock(org.pknu.weather.location.entity.Location.class);
        LocalDateTime presentationTime = LocalDateTime.of(2026, 5, 14, 12, 0);

        Weather oldWeather = weather(10, 20, RainType.NONE, SkyType.CLEAR, presentationTime);
        Weather newWeather = weather(25, 70, RainType.RAIN, SkyType.CLOUDY, presentationTime);
        Map<LocalDateTime, Weather> oldWeatherMap = new HashMap<>();
        oldWeatherMap.put(presentationTime, oldWeather);

        when(locationRepository.safeFindById(locationId)).thenReturn(location);
        when(weatherApi.getVillageShortTermForecast(location)).thenReturn(List.of(newWeather));
        when(weatherRepository.findAllByLocationAfterNow(location)).thenReturn(oldWeatherMap);

        weatherRefreshAsyncService.updateAndCache(locationId);

        verify(weatherRepository).batchUpsert(List.of(oldWeather), location);
        verify(weatherCacheService).updateCachedWeathersForLocation(locationId, List.of(oldWeather));
        org.assertj.core.api.Assertions.assertThat(oldWeather.getTemperature()).isEqualTo(25);
        org.assertj.core.api.Assertions.assertThat(oldWeather.getHumidity()).isEqualTo(70);
        org.assertj.core.api.Assertions.assertThat(oldWeather.getRainType()).isEqualTo(RainType.RAIN);
        org.assertj.core.api.Assertions.assertThat(oldWeather.getSkyType()).isEqualTo(SkyType.CLOUDY);
    }

    @Test
    void refreshCacheFromDb_loadsDbSnapshotThenUpdatesCache() {
        Long locationId = 1L;
        List<Weather> forecast = List.of(Weather.builder().build());
        when(weatherQueryService.getWeathers(locationId)).thenReturn(forecast);

        weatherRefreshAsyncService.refreshCacheFromDb(locationId);

        InOrder inOrder = inOrder(weatherQueryService, weatherCacheService);
        inOrder.verify(weatherQueryService).getWeathers(locationId);
        inOrder.verify(weatherCacheService).updateCachedWeathersForLocation(locationId, forecast);
        verifyNoInteractions(weatherRepository, weatherApi, locationRepository);
    }

    @Test
    void refreshCacheFromForecast_updatesCacheWithoutDbLookup() {
        Long locationId = 1L;
        List<Weather> forecast = List.of(Weather.builder().build());

        weatherRefreshAsyncService.refreshCacheFromForecast(locationId, forecast);

        InOrder inOrder = inOrder(weatherCacheService);
        inOrder.verify(weatherCacheService).updateCachedWeathersForLocation(locationId, forecast);
        verifyNoInteractions(weatherRepository, weatherQueryService, weatherApi, locationRepository);
    }

    private Weather weather(Integer temperature, Integer humidity, RainType rainType, SkyType skyType,
                            LocalDateTime presentationTime) {
        return Weather.builder()
                .presentationTime(presentationTime)
                .basetime(presentationTime.minusHours(1))
                .windSpeed(3.5)
                .humidity(humidity)
                .rainProb(40)
                .rain(0.0f)
                .rainType(rainType)
                .temperature(temperature)
                .sensibleTemperature(temperature.doubleValue())
                .snowCover(0.0f)
                .skyType(skyType)
                .build();
    }
}
