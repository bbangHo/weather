package org.pknu.weather.weather.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.weather.dto.WeatherRedisDTO;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class WeatherRedisConverter {

    private final ObjectMapper objectMapper;

    /**
     * DTO 객체를 순수 JSON 문자열로 변환합니다.
     */
    public String toJson(WeatherRedisDTO.WeatherData data) {
        if (data == null) return null;
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Weather 데이터 직렬화 에러: {}", data, e);
            return null;
        }
    }

    /**
     * JSON 문자열을 DTO 객체로 변환합니다.
     */
    public WeatherRedisDTO.WeatherData fromJson(String jsonStr) {
        if (jsonStr == null || jsonStr.isBlank()) return null;
        try {
            return objectMapper.readValue(jsonStr, WeatherRedisDTO.WeatherData.class);
        } catch (JsonProcessingException e) {
            log.error("Weather JSON 파싱 에러 (데이터 손상): {}", jsonStr, e);
            return null;
        }
    }
}
