package org.pknu.weather.weather.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.feignclient.weatherapi.target.WeatherApi;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherRefreshAsyncService {

    private final LocationRepository locationRepository;
    private final WeatherRepository weatherRepository;
    private final WeatherCacheService weatherCacheService;
    private final WeatherQueryService weatherQueryService;
    private final WeatherApi weatherApi;

    @Async("WeatherCUDExecutor")
    @Transactional
    public void createAndCache(Long locationId) {
        runWithFailureLog("createAndCache", locationId, () -> {
            Location location = locationRepository.safeFindById(locationId);
            List<Weather> forecast = weatherApi.getVillageShortTermForecast(location);
            weatherRepository.batchUpsert(forecast, location);
            weatherCacheService.updateCachedWeathersForLocation(locationId, forecast);
            weatherQueryService.markWeatherCreated(locationId);
        });
    }

    @Async("WeatherCUDExecutor")
    @Transactional
    public void createAndCache(Long locationId, List<Weather> forecast) {
        runWithFailureLog("createAndCacheWithForecast", locationId, () -> {
            Location location = locationRepository.safeFindById(locationId);
            weatherRepository.batchUpsert(forecast, location);
            weatherCacheService.updateCachedWeathersForLocation(locationId, forecast);
            weatherQueryService.markWeatherCreated(locationId);
        });
    }

    @Async("WeatherCUDExecutor")
    @Transactional
    public void updateAndCache(Long locationId) {
        runWithFailureLog("updateAndCache", locationId, () -> {
            Location location = locationRepository.safeFindById(locationId);
            List<Weather> forecast = weatherApi.getVillageShortTermForecast(location);
            List<Weather> updatedForecast = updateWeatherList(location, forecast);
            weatherRepository.batchUpsert(updatedForecast, location);
            weatherCacheService.updateCachedWeathersForLocation(locationId, updatedForecast);
            weatherQueryService.markWeatherUpdated(locationId);
        });
    }

    @Async("WeatherCUDExecutor")
    @Transactional
    public void updateAndCache(Long locationId, List<Weather> forecast) {
        runWithFailureLog("updateAndCacheWithForecast", locationId, () -> {
            Location location = locationRepository.safeFindById(locationId);
            List<Weather> updatedForecast = updateWeatherList(location, forecast);
            weatherRepository.batchUpsert(updatedForecast, location);
            weatherCacheService.updateCachedWeathersForLocation(locationId, updatedForecast);
            weatherQueryService.markWeatherUpdated(locationId);
        });
    }

    @Async("WeatherCUDExecutor")
    public void refreshCacheFromDb(Long locationId) {
        runWithFailureLog("refreshCacheFromDb", locationId, () -> {
            List<Weather> forecast = weatherQueryService.getWeathers(locationId);
            weatherCacheService.updateCachedWeathersForLocation(locationId, forecast);
        });
    }

    @Async("WeatherCUDExecutor")
    public void refreshCacheFromForecast(Long locationId, List<Weather> forecast) {
        runWithFailureLog("refreshCacheFromForecast", locationId, () ->
                weatherCacheService.updateCachedWeathersForLocation(locationId, forecast)
        );
    }

    private List<Weather> updateWeatherList(Location location, List<Weather> forecast) {
        Map<java.time.LocalDateTime, Weather> oldWeatherMap = weatherRepository.findAllByLocationAfterNow(location);
        forecast.forEach(newWeather -> {
            Weather oldWeather = oldWeatherMap.get(newWeather.getPresentationTime());
            if (oldWeather != null) {
                oldWeather.updateWeather(newWeather);
                return;
            }

            newWeather.addLocation(location);
            oldWeatherMap.put(newWeather.getPresentationTime(), newWeather);
        });

        return oldWeatherMap.values().stream().toList();
    }

    private void runWithFailureLog(String operation, Long locationId, Runnable task) {
        try {
            task.run();
        } catch (RuntimeException e) {
            log.warn("Weather refresh failed. operation={}, locationId={}", operation, locationId, e);
            throw e;
        }
    }
}
