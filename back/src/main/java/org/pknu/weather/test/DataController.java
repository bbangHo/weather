package org.pknu.weather.test;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.ApiResponse;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.converter.WeatherConverter;
import org.pknu.weather.weather.dto.WeatherRedisDTO;
import org.pknu.weather.weather.feignclient.weatherapi.target.WeatherApi;
import org.pknu.weather.weather.repository.WeatherRedisRepository;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.pknu.weather.weather.service.WeatherService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class DataController {
    private final DataService dataService;

    @DeleteMapping("/api/weather")
    public ApiResponse<Object> deleteCacheData(@RequestHeader("Authorization") String authorization) {
        return dataService.deleteCacheData();
    }

    @PostMapping("/api/weather")
    public ApiResponse<Object> postCacheData(@RequestHeader("Authorization") String authorization) {
        return dataService.postCacheData();
    }

    @PatchMapping("/api/db/weather")
    public ApiResponse<Object> weatherDataPatch(@RequestHeader("Authorization") String authorization) {
        dataService.weatherDataPatch();
        return ApiResponse.onSuccess();
    }

    @PatchMapping("/api/db/weather400")
    public ApiResponse<Object> weatherDataPatch400(@RequestHeader("Authorization") String authorization) {
        dataService.postCacheData400();
        return ApiResponse.onSuccess();
    }

    @PostMapping("/api/db/weather")
    public ApiResponse<Object> weatherDataPost(@RequestHeader("Authorization") String authorization) {
        dataService.weatherDataPost();
        return ApiResponse.onSuccess();
    }
}
