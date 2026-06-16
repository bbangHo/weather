package org.pknu.weather.mainpage.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.post.dto.PostResponse;
import org.pknu.weather.post.dto.TagDto;
import org.pknu.weather.post.service.PostQueryService;
import org.pknu.weather.tag.service.TagQueryService;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.converter.WeatherResponseConverter;
import org.pknu.weather.weather.dto.WeatherResponseDTO;
import org.pknu.weather.weather.event.WeatherCreateEvent;
import org.pknu.weather.weather.event.WeatherUpdateEvent;
import org.pknu.weather.weather.feignclient.weatherapi.target.WeatherApi;
import org.pknu.weather.weather.service.ActiveWeatherLocationService;
import org.pknu.weather.weather.service.WeatherCacheService;
import org.pknu.weather.weather.service.WeatherQueryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메인페이지에서 사용되는 API를 위한 서비스 즉, 화면에 맞춰진 로직을 관리한다. 해당 서비스는 서비스를 의존할 수 있다. 단 핵심 비즈니스 로직만 의존한다. 서비스를 참조하는 서비스를 한 곳으로 몰아서
 * 서비스간 순환 참조를 방지한다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MainPageService {
    private final MemberRepository memberRepository;
    private final WeatherQueryService weatherQueryService;
    private final LocationRepository locationRepository;
    private final PostQueryService postQueryService;
    private final TagQueryService tagQueryService;
    private final WeatherCacheService weatherCacheService;
    private final WeatherApi weatherApi;
    private final ActiveWeatherLocationService activeWeatherLocationService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 메인 페이지에 날씨와 관련된 데이터를 반환한다. 만약 해당 지역의 날씨의 갱신 시간이 지났다면 갱신을 시도하고 반환한다. 만약 해당 지역의 날씨 정보가 없다면 저장하고 반환한다.
     *
     * @param email
     * @return
     */
    public WeatherResponseDTO.MainPageWeatherData getWeatherInfo(String email, Long locationId) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = resolveLocation(member, locationId);
        List<Weather> weatherList = getWeatherInfo(location);

        return WeatherResponseConverter.toMainPageWeatherData(weatherList, member);
    }

    private Location resolveLocation(Member member, Long locationId) {
        return locationId != null
                ? locationRepository.safeFindById(locationId)
                : member.getLocation();
    }

    /**
     * 조회된 지역을 활성 지역으로 기록한 뒤, 캐시가 없거나 갱신이 필요하면 외부 API 값을 즉시 반환한다.
     * 외부 API 응답은 이벤트에 함께 넘겨 비동기 DB 저장과 Redis 캐시 갱신에 재사용한다.
     */
    private List<Weather> getWeatherInfo(Location location) {
        activeWeatherLocationService.markRequestedLocation(location.getId());

        List<Weather> cachedWeatherList = weatherCacheService.getCachedWeathers(location.getId());
        if (!cachedWeatherList.isEmpty()) {
            return cachedWeatherList;
        }

        if (!weatherQueryService.weatherHasBeenCreated(location)) {
            return createWeather(location);
        }

        if (!weatherQueryService.weatherHasBeenUpdated(location)) {
            updateWeather(location);
        }

        return weatherQueryService.getWeathers(location.getId());
    }

    /**
     * 최초 조회 지역은 요청 스레드에서 외부 API를 호출해 바로 응답하고, 저장은 비동기 이벤트로 위임한다.
     */
    private List<Weather> createWeather(Location location) {
        log.info("createWeather locationId: {}", location.getId());
        List<Weather> forecast = weatherApi.getVillageShortTermForecast(location);
        eventPublisher.publishEvent(new WeatherCreateEvent(location.getId(), forecast));
        return forecast;
    }

    /**
     * stale 상태의 지역은 최신 외부 API 값을 바로 응답하고, 같은 forecast로 비동기 저장/캐시 갱신을 수행한다.
     */
    private void updateWeather(Location location) {
        log.info("updateWeather locationId: {}", location.getId());
        eventPublisher.publishEvent(new WeatherUpdateEvent(location.getId()));
    }

    /**
     * 사용자의 지역에서 가장 최근에 작성된 글 5개를 반환한다.
     *
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public List<PostResponse.Post> getLatestPostList(String email) {
        return postQueryService.getLatestPostList(email);
    }

    @Transactional(readOnly = true)
    public List<TagDto.SimpleTag> getMostSelectedTags(String email) {
        return tagQueryService.getMostSelectedTags(email);
    }

    @Transactional(readOnly = true)
    public WeatherResponseDTO.SimpleRainInformation getSimpleRainInfo(String email) {
        return weatherQueryService.getSimpleRainInfo(email);
    }
}
