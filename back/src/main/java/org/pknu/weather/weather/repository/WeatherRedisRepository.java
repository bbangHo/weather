package org.pknu.weather.weather.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.weather.dto.WeatherRedisDTO;
import org.pknu.weather.weather.utils.WeatherRedisConverter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.pknu.weather.weather.utils.WeatherRedisKeyUtils.buildKey;
import static org.pknu.weather.weather.utils.WeatherRedisKeyUtils.generateHourlyWeatherKeys;

@RequiredArgsConstructor
@Slf4j
@Repository
public class WeatherRedisRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final WeatherRedisConverter weatherRedisConverter;

    private final Duration DEFAULT_DURATION = Duration.ofHours(24);
    private final Integer DEFAULT_HOURS = 24;

    // 다건 조회 로직
    public List<WeatherRedisDTO.WeatherData> multiGetWeathers(Long locationId, LocalDateTime localDateTime) {
        List<String> result = Optional.ofNullable(
                stringRedisTemplate.opsForValue().multiGet(generateHourlyWeatherKeys(locationId, localDateTime, DEFAULT_HOURS))
        ).orElse(Collections.emptyList());

        return result.stream()
                .filter(str -> str != null && !str.isBlank())
                .map(weatherRedisConverter::fromJson)
                .filter(Objects::nonNull)
                .toList();
    }

    public void saveWeather(Long locationId, WeatherRedisDTO.WeatherData weatherData, Duration duration) {
        String jsonStr = weatherRedisConverter.toJson(weatherData);
        if (jsonStr != null) {
            stringRedisTemplate.opsForValue().set(buildKey(locationId, weatherData.getPresentationTime()), jsonStr, duration);
        }
    }

    public void saveWeatherList(Long locationId, List<WeatherRedisDTO.WeatherData> weatherDataList) {
        for (WeatherRedisDTO.WeatherData weatherData : weatherDataList) {
            saveWeather(locationId, weatherData, DEFAULT_DURATION);
        }
    }

    public void updateWeather(Long locationId, WeatherRedisDTO.WeatherData weatherData) {
        saveWeather(locationId, weatherData, DEFAULT_DURATION);
    }

    public void deleteValues(Long locationId, LocalDateTime presentationTime) {
        stringRedisTemplate.delete(buildKey(locationId, presentationTime));
    }

    // 리스트 조회
    public List<WeatherRedisDTO.WeatherData> getWeatherList(Long locationId) {
        try {
            List<String> jsonList = stringRedisTemplate.opsForList().range(buildKey(locationId), 0, -1);

            if (jsonList == null || jsonList.isEmpty()) {
                log.info("Redis에 데이터가 없습니다. Key: {}", buildKey(locationId));
                return Collections.emptyList();
            }

            return jsonList.stream()
                    .filter(str -> str != null && !str.isBlank())
                    .map(weatherRedisConverter::fromJson)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            log.error("Redis 통신 에러: ", e);
            return Collections.emptyList();
        }
    }

    public void rightPushAll(Long locationId, List<WeatherRedisDTO.WeatherData> weatherDataList) {
        List<String> jsonList = weatherDataList.stream()
                .map(weatherRedisConverter::toJson)
                .filter(Objects::nonNull)
                .toList();

        if (!jsonList.isEmpty()) {
            stringRedisTemplate.opsForList().rightPushAll(buildKey(locationId), jsonList);
            stringRedisTemplate.expire(buildKey(locationId), DEFAULT_DURATION);
        }
    }

    public void updateWeatherList(Long locationId, List<WeatherRedisDTO.WeatherData> weatherDataList) {
        stringRedisTemplate.delete(buildKey(locationId));
        rightPushAll(locationId, weatherDataList);
    }

    public void deleteWeatherList(Long locationId) {
        stringRedisTemplate.delete(buildKey(locationId));
    }

    public void expireWeather(Long locationId, LocalDateTime presentationTime, int timeout) {
        stringRedisTemplate.expire(buildKey(locationId, presentationTime), timeout, TimeUnit.HOURS);
    }
}