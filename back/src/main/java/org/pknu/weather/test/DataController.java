package org.pknu.weather.test;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.ApiResponse;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.weather.repository.WeatherRedisRepository;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class DataController {
    private final WeatherRepository weatherRepository;
    private final LocationRepository locationRepository;
    private final WeatherRedisRepository weatherRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EntityManager em;

    @Transactional
    @DeleteMapping("/api/weather")
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
}
