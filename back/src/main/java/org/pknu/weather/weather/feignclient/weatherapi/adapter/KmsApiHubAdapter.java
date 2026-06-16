package org.pknu.weather.weather.feignclient.weatherapi.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.feignclient.dto.PointDTO;
import org.pknu.weather.weather.feignclient.weatherapi.KmaApiHubFeignClient;
import org.pknu.weather.weather.feignclient.weatherapi.dto.Item;
import org.pknu.weather.weather.feignclient.weatherapi.dto.KmsApiHubParamDTO;
import org.pknu.weather.weather.feignclient.weatherapi.dto.KmsApiHubResponseDTO;
import org.pknu.weather.weather.feignclient.weatherapi.exception.ForecastNotAvailableException;
import org.pknu.weather.weather.feignclient.weatherapi.target.WeatherApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component("KMA_API_HUB")
@RequiredArgsConstructor
@Slf4j
public class KmsApiHubAdapter implements WeatherApi {

    private final KmaApiHubFeignClient kmaApiHubFeignClient;

    @Value("${api.weather.kmshub-service-key}")
    private String weatherServiceKey;

    @Value("${api.weather.base-time-fallback-attempts:3}")
    private int baseTimeFallbackAttempts;

    private final RetryTemplate retryTemplate;

    /**
     * 사용자의 위도 경도 및 기타 정보를 받아와 weather로 반환한다. api 호출 실패시 baseTime을 3시간 늦추고 재시도합니다
     *
     * @return now ~ 24 시간의 Wether 엔티티를 담고있는 List
     * @Location 사용자 위치 엔티티
     */
    public List<Weather> getVillageShortTermForecast(Location location) {
        float lon = location.getLongitude().floatValue();
        float lat = location.getLatitude().floatValue();

        PointDTO pointDTO = GeometryUtils.coordinateToPoint(lon, lat);
        LocalDateTime baseLocalDateTime = LocalDateTime.now();

        for (int fallbackAttempt = 0; fallbackAttempt < fallbackAttempts(); fallbackAttempt++) {
            int currentFallbackAttempt = fallbackAttempt;
            LocalDateTime requestBaseTime = baseLocalDateTime.minusHours(3L * fallbackAttempt);
            try {
                return retryTemplate.execute(context ->
                        requestForecast(location, pointDTO, requestBaseTime, context.getRetryCount(), currentFallbackAttempt)
                );
            } catch (ForecastNotAvailableException e) {
                log.warn("Forecast baseTime is not available. locationId={}, fallbackAttempt={}",
                        location.getId(), fallbackAttempt, e);
            }
        }

        throw new GeneralException(ErrorStatus._API_SERVER_ERROR);
    }

    private List<Weather> requestForecast(
            Location location,
            PointDTO pointDTO,
            LocalDateTime requestBaseTime,
            int retryCount,
            int fallbackAttempt
    ) {
        String date = DateTimeFormatter.getFormattedBaseDate(requestBaseTime);
        String time = DateTimeFormatter.getFormattedBaseTime(requestBaseTime);

        KmsApiHubParamDTO kmsApiHubParamDTO = createParam(weatherServiceKey, date, time, pointDTO);

        log.info("Forecast API locationId={}, x={}, y={}, date={}, time={}, retryCount={}, fallbackAttempt={}",
                location.getId(),
                pointDTO.getX() != null ? pointDTO.getX() : "N/A",
                pointDTO.getY() != null ? pointDTO.getY() : "N/A",
                date,
                time,
                retryCount,
                fallbackAttempt);

        KmsApiHubResponseDTO kmsApiHubResponseDTO = kmaApiHubFeignClient.getVillageShortTermForecast(kmsApiHubParamDTO);
        List<Item> itemList = extractItems(kmsApiHubResponseDTO, location.getId(), date, time);

        return toWeatherList(itemList, date, time);
    }

    private List<Item> extractItems(KmsApiHubResponseDTO response, Long locationId, String date, String time) {
        List<Item> itemList = Optional.ofNullable(response)
                .map(KmsApiHubResponseDTO::getResponse)
                .map(KmsApiHubResponseDTO.Response::getBody)
                .map(KmsApiHubResponseDTO.Body::getItems)
                .map(KmsApiHubResponseDTO.Body.Items::getItemList)
                .orElseThrow(() -> new ForecastNotAvailableException(
                        "Forecast data is not available. locationId=" + locationId + ", date=" + date + ", time=" + time
                ));

        if (itemList.isEmpty()) {
            throw new ForecastNotAvailableException(
                    "Forecast data is empty. locationId=" + locationId + ", date=" + date + ", time=" + time
            );
        }

        return itemList;
    }

    private int fallbackAttempts() {
        return Math.max(baseTimeFallbackAttempts, 1);
    }

    /**
     * 단기 날씨 예보 API 에서 얻은 데이터를 Weather 데이터로 가공하는 메서드
     *
     * @param itemList 단기 날씨 예보 API 에서 얻은 데이터
     * @param date     ex. "202409"
     * @param time     ex. "0500"
     * @return
     */
    private List<Weather> toWeatherList(List<Item> itemList, String date, String time) {
        Map<String, Weather> weatherMap = new HashMap<>();
        LocalDateTime baseTime = DateTimeFormatter.formattedDateTime2LocalDateTime(date, time);

        for (Item item : itemList) {
            String fcstTime = item.getFcstTime();
            LocalDateTime presentationTime = DateTimeFormatter.formattedDateTime2LocalDateTime(date, fcstTime);

            if (presentationTime.isBefore(baseTime)) {
                presentationTime = presentationTime.plusDays(1L);
            }

            if (!weatherMap.containsKey(fcstTime)) {
                Weather weather = Weather.builder()
                        .basetime(baseTime)
                        .presentationTime(presentationTime)
                        .build();

                weatherMap.put(fcstTime, weather);
            }

            Weather weather = weatherMap.get(fcstTime);
            weather.categoryClassify(item);
            weatherMap.put(fcstTime, weather);
        }

        return weatherMap.values().stream()
                .filter(weather -> weather.getPresentationTime().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Weather::getPresentationTime))
                .toList();
    }

    private KmsApiHubParamDTO createParam(String authKey, String baseDate, String baseTime, PointDTO pointDTO) {
        return KmsApiHubParamDTO.builder()
                .authKey(authKey)
                .pageNo(1)
                .numOfRows(288)
                .base_date(baseDate)
                .base_time(baseTime)
                .nx(pointDTO.getX())
                .ny(pointDTO.getY())
                .build();
    }
}
