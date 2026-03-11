package org.pknu.weather.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.post.service.PostQueryService;
import org.pknu.weather.tag.service.TagQueryService;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.converter.WeatherResponseConverter;
import org.pknu.weather.weather.dto.WeatherResponseDTO;
import org.pknu.weather.weather.event.WeatherCacheRefreshEvent;
import org.pknu.weather.weather.event.WeatherCreateEvent;
import org.pknu.weather.weather.event.WeatherUpdateEvent;
import org.pknu.weather.weather.feignclient.weatherapi.target.WeatherApi;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.pknu.weather.weather.service.WeatherCacheService;
import org.pknu.weather.weather.service.WeatherQueryService;
import org.pknu.weather.weather.service.WeatherService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestMainPageService {
    private final MemberRepository memberRepository;
    private final WeatherQueryService weatherQueryService;
    private final LocationRepository locationRepository;
    private final WeatherApi weatherApi;
    private final ApplicationEventPublisher eventPublisher;
    private final WeatherCacheService weatherCacheService;
    private final WeatherService weatherService;
    private final WeatherRepository weatherRepository;

    /**
     * 개선전 ver1
     *
     * @param email
     * @return
     */
    @Transactional
    public WeatherResponseDTO.MainPageWeatherData getWeatherInfoV1(String email, Long locationId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Location location = member.getLocation();

        List<Weather> weatherList = new ArrayList<>();

        // 해당 지역에 날씨 예보가 있는지 없는지 체크
        if (!weatherRepository.weatherHasBeenCreated(location)) {
            weatherList = weatherService.saveWeathers(location);
            return WeatherResponseConverter.toMainPageWeatherData(weatherList, member);
        }

        // 예보를 갱신할 시간이 되었는지 체크
        if (!weatherRepository.weatherHasBeenUpdated(location)) {
            weatherService.updateWeathers(location.getId());
        }

        weatherList = weatherQueryService.getWeathers(location.getId());
        return WeatherResponseConverter.toMainPageWeatherData(weatherList, member);
    }

    /**
     * 개선1 ver2
     *
     * @param email
     * @return
     */
    @Transactional
    public WeatherResponseDTO.MainPageWeatherData getWeatherInfoV2(String email, Long locationId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Location location = resolveLocation(member, locationId);

        List<Weather> weatherList = createWeatherIfRequiredNotCached(location);
        if (weatherList != null) {
            return WeatherResponseConverter.toMainPageWeatherData(weatherList, member);
        }

        updateWeatherIfRequiredNotCached(location);
        weatherList = weatherQueryService.getWeathers(location.getId());
        return WeatherResponseConverter.toMainPageWeatherData(weatherList, member);
    }

    /**
     * 개선3 ver3
     *
     * @param email
     * @return
     */
    public WeatherResponseDTO.MainPageWeatherData getWeatherInfoV3(String email, Long locationId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Location location = resolveLocation(member, locationId);

        List<Weather> cachedWeatherList = weatherCacheService.getCachedWeathers(location.getId());
        if (!cachedWeatherList.isEmpty()) {
            return WeatherResponseConverter.toMainPageWeatherData(cachedWeatherList, member);
        }

        List<Weather> weatherList = createWeatherIfRequiredNotCached(location);
        if (weatherList != null) {
            return WeatherResponseConverter.toMainPageWeatherData(weatherList, member);
        }

        updateWeatherIfRequiredNotCached(location);
        weatherList = weatherQueryService.getWeathers(location.getId());

        return WeatherResponseConverter.toMainPageWeatherData(weatherList, member);
    }

    /**
     * 개선4 ver4
     *
     * @param email
     * @return
     */
    public WeatherResponseDTO.MainPageWeatherData getWeatherInfoV4(String email, Long locationId) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = resolveLocation(member, locationId);

        List<Weather> cachedWeatherList = weatherCacheService.getCachedWeathers(location.getId());
        if (!cachedWeatherList.isEmpty()) {
            return WeatherResponseConverter.toMainPageWeatherData(cachedWeatherList, member);
        }

        List<Weather> weatherList = createWeatherIfRequired(location);
        if (weatherList != null) {
            return WeatherResponseConverter.toMainPageWeatherData(weatherList, member);
        }

        updateWeatherIfRequired(location);
        weatherList = weatherQueryService.getWeathers(location.getId());

        return WeatherResponseConverter.toMainPageWeatherData(weatherList, member);
    }

    private Location resolveLocation(Member member, Long locationId) {
        return locationId != null
                ? locationRepository.safeFindById(locationId)
                : member.getLocation();
    }

    private List<Weather> createWeatherIfRequired(Location location) {
        if (!weatherQueryService.weatherHasBeenCreated(location)) {
            List<Weather> newForecast = weatherApi.getVillageShortTermForecast(location);
            eventPublisher.publishEvent(new WeatherCreateEvent(location.getId(), newForecast));
            eventPublisher.publishEvent(new WeatherCacheRefreshEvent(location.getId()));
            return newForecast;
        }
        return null;
    }

    private List<Weather> createWeatherIfRequiredNotCached(Location location) {
        if (!weatherRepository.weatherHasBeenCreated(location)) {
            List<Weather> newForecast = weatherApi.getVillageShortTermForecast(location);
            eventPublisher.publishEvent(new WeatherCreateEvent(location.getId(), newForecast));
            eventPublisher.publishEvent(new WeatherCacheRefreshEvent(location.getId()));
            return newForecast;
        }
        return null;
    }

    private void updateWeatherIfRequired(Location location) {
        log.info("updateWeatherIfRequired lid:" + location.getId());
        if (!weatherQueryService.weatherHasBeenUpdated(location)) {
            eventPublisher.publishEvent(new WeatherUpdateEvent(location.getId()));
            eventPublisher.publishEvent(new WeatherCacheRefreshEvent(location.getId()));
        }
    }

    private void updateWeatherIfRequiredNotCached(Location location) {
        if (!weatherRepository.weatherHasBeenUpdated(location)) {
            eventPublisher.publishEvent(new WeatherUpdateEvent(location.getId()));
            eventPublisher.publishEvent(new WeatherCacheRefreshEvent(location.getId()));
        }
    }
}
