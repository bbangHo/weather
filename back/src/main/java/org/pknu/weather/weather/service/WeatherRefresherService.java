package org.pknu.weather.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.weather.ExtraWeather;
import org.pknu.weather.weather.dto.WeatherResponseDTO;
import org.pknu.weather.weather.event.WeatherCacheRefreshEvent;
import org.pknu.weather.weather.event.WeatherEvent;
import org.pknu.weather.weather.event.WeatherUpdateEvent;
import org.pknu.weather.weather.feignclient.utils.ExtraWeatherApiUtils;
import org.pknu.weather.weather.repository.ExtraWeatherRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.pknu.weather.location.converter.LocationConverter.toLocationDTO;
import static org.pknu.weather.weather.converter.ExtraWeatherConverter.toExtraWeather;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WeatherRefresherService {

    private final LocationRepository locationRepository;
    private final ExtraWeatherRepository extraWeatherRepository;
    private final ExtraWeatherApiUtils extraWeatherApiUtils;
    private final WeatherQueryService weatherQueryService;
    private final WeatherService weatherService;
    private final ActiveWeatherLocationService activeWeatherLocationService;
    private final ApplicationEventPublisher eventPublisher;

    public void refresh(Set<Long> locationIds) {
        List<Location> locations = locationRepository.findByIdIn(locationIds);
        for (Location location : locations) {
            updateWeather(location);
            updateExtraWeather(location);
        }
    }

    private void updateWeather(Location location) {

        if (!weatherQueryService.weatherHasBeenCreated(location)) {
            weatherService.saveWeathers(location);
        }

        if (!weatherQueryService.weatherHasBeenUpdated(location)) {
            weatherService.updateWeathers(location.getId());
        }
    }

    public void updateExtraWeather(Location location) {
        extraWeatherRepository.findByLocationId(location.getId())
                .ifPresentOrElse(
                        extraWeather -> updateExistingExtraWeather(location, extraWeather),
                        () -> saveExtraWeather(location)
                );
    }

    private void updateExistingExtraWeather(Location location, ExtraWeather extraWeather) {
        if (extraWeather.getBasetime().isBefore(LocalDateTime.now().minusHours(3))) {
            WeatherResponseDTO.ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(
                    toLocationDTO(location), extraWeather.getBasetime());
            extraWeather.updateExtraWeather(extraWeatherInfo);
            extraWeatherRepository.save(extraWeather);
        }
    }

    private void saveExtraWeather(Location location) {
        WeatherResponseDTO.ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(
                toLocationDTO(location));

        extraWeatherRepository.save(toExtraWeather(location, extraWeatherInfo));
    }

    /**
     * WeatherUpdateScheduler에 의해 스케쥴링으로 실행됩니다.
    */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateWeatherDataScheduled(Integer limitSize) {
        updateWeatherDataScheduled(limitSize, 0);
    }

    /**
     * 전체 지역 대신 ActiveWeatherLocationService가 선별한 활성 지역만 갱신 이벤트로 발행한다.
     * dispatchIntervalMillis로 이벤트 발행 속도를 낮춰 외부 API와 async executor 큐에 걸리는 부하를 조절한다.
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateWeatherDataScheduled(Integer limitSize, long dispatchIntervalMillis) {
        List<Long> activeLocationIds = activeWeatherLocationService.getActiveLocationIds(limitSize);
        publishEvents(activeLocationIds, WeatherUpdateEvent::new, dispatchIntervalMillis);
        log.info("Scheduled weather refresh active locations: count={}, ids={}", activeLocationIds.size(), activeLocationIds);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateWeatherCachedDataScheduled(Integer limitSize) {
        updateWeatherCachedDataScheduled(limitSize, 0);
    }

    /**
     * 활성 지역의 Redis 캐시만 DB 기준으로 다시 적재할 때 사용하는 보조 스케줄 경로다.
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateWeatherCachedDataScheduled(Integer limitSize, long dispatchIntervalMillis) {
        // TODO: 갱신 조건 고민 redis에서 zset으로 지역별 인기순 vs db에서 최근 갱신된 지역 인기순
        // 혹시 조건이 바뀔 수도 있어 위의 메서드와 분리
        List<Long> activeLocationIds = activeWeatherLocationService.getActiveLocationIds(limitSize);
        publishEvents(activeLocationIds, WeatherCacheRefreshEvent::new, dispatchIntervalMillis);

        log.info("캐싱된 지역 수: {}, 지역 ids: {}", activeLocationIds.size(), activeLocationIds);
    }

    /**
     * 이벤트를 순차 발행하면서 필요하면 각 발행 사이에 짧은 대기 시간을 둔다.
     */
    private void publishEvents(List<Long> locationIds, Function<Long, WeatherEvent> eventFactory, long dispatchIntervalMillis) {
        for (Long locationId : locationIds) {
            publishEvent(eventFactory.apply(locationId));
            throttle(dispatchIntervalMillis);
        }
    }

    private void publishEvent(WeatherEvent event) {
        try {
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.warn("이벤트 처리 중 예외 발생. locationId: {}", event.getLocationId(), e);
        }
    }

    /**
     * 스케줄러가 한 번에 너무 많은 비동기 작업을 밀어 넣지 않도록 발행 속도를 제한한다.
     */
    private void throttle(long dispatchIntervalMillis) {
        if (dispatchIntervalMillis <= 0) {
            return;
        }

        try {
            Thread.sleep(dispatchIntervalMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Weather update dispatch interrupted", e);
        }
    }
}
