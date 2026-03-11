package org.pknu.weather.test;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.ApiResponse;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.converter.WeatherConverter;
import org.pknu.weather.weather.dto.WeatherRedisDTO;
import org.pknu.weather.weather.feignclient.weatherapi.target.WeatherApi;
import org.pknu.weather.weather.repository.WeatherRedisRepository;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.pknu.weather.weather.service.WeatherService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataService {
    private final WeatherRepository weatherRepository;
    private final LocationRepository locationRepository;
    private final WeatherRedisRepository weatherRedisRepository;
    private final WeatherApi weatherApi;
    private final WeatherService weatherService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EntityManager em;

    @Transactional
    public ApiResponse<Object> deleteCacheData(@RequestHeader("Authorization") String authorization) {
        List<Location> locationList = em.createQuery(
                        "select m.location from member m where m.id between 1171 and 1670", Location.class
                )
                .getResultList();

        int deleteWeathers = em.createQuery(
                        "delete from Weather w where w.location in :locationList"
                )
                .setParameter("locationList", locationList)
                .executeUpdate();

        Set<String> keys = redisTemplate.keys("weather:location:*");
        Long deleteKeys = redisTemplate.delete(keys);
        Map<String, Object> result = new HashMap<>();
        result.put("weather", deleteWeathers);
        result.put("key", deleteKeys);
        return ApiResponse.onSuccess(result);
    }

    @Transactional
    public ApiResponse<Object> postCacheData(@RequestHeader("Authorization") String authorization) {
        List<Location> locationList = em.createQuery(
                        "select m.location from member m where m.id between 1171 and 1670", Location.class
                )
                .getResultList();

        for (Location location : locationList) {
            List<Weather> weatherList = weatherRepository.findAllInLocationSorted(location.getId(), LocalDateTime.now().plusHours(24)).stream()
                    .sorted(Comparator.comparing(Weather::getPresentationTime))
                    .toList();

            List<WeatherRedisDTO.WeatherData> weatherDataList = WeatherConverter.toWeatherDataList(weatherList);
            weatherRedisRepository.updateWeatherList(location.getId(), weatherDataList);
            log.info("test postCacheData locationId: " + location.getId());
        }

        Set<String> keys = redisTemplate.keys("weather:location:*");
        return ApiResponse.onSuccess(keys.size());
    }

    public void weatherDataPatch(@RequestHeader("Authorization") String authorization) {
        List<Location> locationList = em.createQuery(
                        "select m.location from member m where m.id between 1171 and 1670", Location.class
                )
                .getResultList();

        for (Location location : locationList) {
            List<Weather> weatherList = new ArrayList<>();
            try {
                weatherList = weatherApi.getVillageShortTermForecast(location);
            } catch (Exception e) {

            }

            weatherService.bulkUpdateWeathersAsync(location.getId(), weatherList);
        }

        ApiResponse.onSuccess();
    }

    public void weatherDataPost(@RequestHeader("Authorization") String authorization) {
        List<Location> locationList = em.createQuery(
                        "select m.location from member m where m.id between 1171 and 1670", Location.class
                )
                .getResultList();

        for (Location location : locationList) {
            List<Weather> weatherList = weatherApi.getVillageShortTermForecast(location);
            weatherService.saveWeathersAsync(location.getId(), weatherList);
        }

        ApiResponse.onSuccess();
    }
}
