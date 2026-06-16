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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
    private final String REQUESTED_LOCATION_KEY = "weather:active:requested-locations";
    private final String CACHED_LOCATION_KEY = "weather:active:cached-locations";

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
        markCachedLocation(locationId);
    }

    public void deleteWeatherList(Long locationId) {
        stringRedisTemplate.delete(buildKey(locationId));
    }

    public void expireWeather(Long locationId, LocalDateTime presentationTime, int timeout) {
        stringRedisTemplate.expire(buildKey(locationId, presentationTime), timeout, TimeUnit.HOURS);
    }

    /**
     * 사용자 조회가 발생한 지역을 최근 요청 활성 지역으로 기록한다.
     */
    public void markRequestedLocation(Long locationId) {
        if (locationId == null) {
            return;
        }

        stringRedisTemplate.opsForZSet().add(REQUESTED_LOCATION_KEY, String.valueOf(locationId), System.currentTimeMillis());
        stringRedisTemplate.expire(REQUESTED_LOCATION_KEY, DEFAULT_DURATION);
    }

    /**
     * 설정된 window 밖의 요청 기록은 제거하고, 최신 요청 순으로 지역 ID를 반환한다.
     * 즉, ${weather.update.recent-request-window-hours} 시간 이내에 조회된 활성 지역을 ${weather.update.limitSize}만큼 가져온다.
     */
    public List<Long> getRecentlyRequestedLocationIds(Duration window, int limitSize) {
        long cutoffMillis = System.currentTimeMillis() - window.toMillis();
        stringRedisTemplate.opsForZSet().removeRangeByScore(REQUESTED_LOCATION_KEY, 0, cutoffMillis);

        Set<String> values = limitSize > 0
                ? stringRedisTemplate.opsForZSet().reverseRange(REQUESTED_LOCATION_KEY, 0, limitSize - 1L)
                : stringRedisTemplate.opsForZSet().reverseRange(REQUESTED_LOCATION_KEY, 0, -1);

        return toLongList(values);
    }

    /**
     * Redis에 날씨 캐시가 적재된 지역을 별도 set에 기록해 스케줄 갱신 후보로 유지한다.
     */
    public void markCachedLocation(Long locationId) {
        if (locationId == null) {
            return;
        }

        stringRedisTemplate.opsForSet().add(CACHED_LOCATION_KEY, String.valueOf(locationId));
        stringRedisTemplate.expire(CACHED_LOCATION_KEY, DEFAULT_DURATION);
    }

    /**
     * 현재 Redis 캐시를 가진 지역 ID를 가져와 활성 지역 후보로 사용한다.
     * 즉, 캐시에 존재하는 활성 지역을 ${weather.update.limitSize}만큼 가져온다.
     */
    public List<Long> getCachedLocationIds(int limitSize) {
        Set<String> values = Optional.ofNullable(stringRedisTemplate.opsForSet().members(CACHED_LOCATION_KEY))
                .orElse(Collections.emptySet());

        return toLongList(values).stream()
                .limit(limitSize > 0 ? limitSize : Long.MAX_VALUE)
                .toList();
    }

    private List<Long> toLongList(Set<String> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }

        LinkedHashSet<Long> locationIds = new LinkedHashSet<>();
        for (String value : values) {
            if (value == null) {
                continue;
            }

            try {
                locationIds.add(Long.valueOf(value));
            } catch (NumberFormatException e) {
                log.warn("Invalid locationId in weather redis active set. value={}", value);
            }
        }

        return locationIds.stream().toList();
    }
}
