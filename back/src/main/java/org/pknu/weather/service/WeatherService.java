package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Member;
import org.pknu.weather.dto.LocationDTO;
import org.pknu.weather.dto.WeatherResponse;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.feignClient.AirConditionClient;
import org.pknu.weather.feignClient.UVClient;
import org.pknu.weather.feignClient.WeatherFeignClient;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.feignClient.utils.ExtraWeatherApiUtils;
import org.pknu.weather.repository.ExtraWeatherRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.feignClient.WeatherFeignClient;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.pknu.weather.dto.converter.LocationConverter.toLocationDTO;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WeatherService {
    private final WeatherFeignClient weatherFeignClient;
    private final WeatherRepository weatherRepository;
    private final ExtraWeatherRepository extraWeatherRepository;
    private final MemberRepository memberRepository;
    private final ExtraWeatherApiUtils extraWeatherApiUtils;
    private final LocationRepository locationRepository;

    /**
     * TODO: 성능 개선 필요
     * 현재 ~ +24시간 까지의 날씨 정보를 불러옵니다.
     *
     * @param location
     * @return
     */
    public List<Weather> getWeathers(Location location) {
        return weatherRepository.findAllWithLocation(location, LocalDateTime.now().plusHours(24)).stream()
                .sorted(Comparator.comparing(Weather::getPresentationTime))
                .toList();
    }

    /**
     * 위도와 경도에 해당하는 지역(읍면동)의 24시간치 날씨 단기 예보 정보를 저장합니다.
     *
     * @return 위도와 경도에 해당하는 Location의 Weather list를 반환
     */
    // TODO: 비동기 처리
    @Transactional
    public List<Weather> saveWeathers(Location location) {
        float lon = location.getLongitude().floatValue();
        float lat = location.getLatitude().floatValue();

        List<Weather> values = weatherFeignClient.preprocess(lon, lat);
        List<Weather> weatherList = new ArrayList<>(values);

        weatherList.forEach(w -> w.addLocation(location));
        return weatherRepository.saveAll(weatherList);
    }

    /**
     * 단기 날씨 예보 API가 3시간 마다 갱신되기 때문에, 날씨 데이터 갱신을 위한 메서드
     *
     * @param loc API를 호출한 사용자의 Location 엔티티
     * @return 해당 위치의 날씨 데이터 List
     */
    @Async("threadPoolTaskExecutor")
    @Transactional
    public void updateWeathers(Location loc) {
        Location location = locationRepository.safeFindById(loc.getId());

        float lon = location.getLongitude().floatValue();
        float lat = location.getLatitude().floatValue();

        List<Weather> newWeatherList = weatherFeignClient.preprocess(lon, lat);

        List<Weather> orderWeatherList = new ArrayList<>(weatherRepository.findAllWithLocation(location, LocalDateTime.now().plusHours(24)).stream()
                .sorted(Comparator.comparing(Weather::getPresentationTime))
                .toList());

        int minLen = Math.min(newWeatherList.size(), orderWeatherList.size());
        int maxLen = Math.max(newWeatherList.size(), orderWeatherList.size());

        for (int i = 0; i < minLen; i++) {
            Weather orderWeather = orderWeatherList.get(i);
            Weather newWeather = newWeatherList.get(i);
            orderWeather.updateWeather(newWeather);
        }

        List<Weather> subList = new ArrayList<>(newWeatherList.subList(minLen, maxLen));
        subList.forEach(weather -> weather.addLocation(location));
        orderWeatherList.addAll(subList);

        weatherRepository.saveAll(orderWeatherList);
    }

    /**
     * 예보 시간이 현재 보다 과거이면 모두 삭제합니다.
     */
    // TODO: 비동기 처리
    public void bulkDeletePastWeather() {
        weatherRepository.bulkDeletePastWeathers();
    }

    @Transactional
    public WeatherResponse.ExtraWeatherInfo extraWeatherInfo(String email){

        Member member = memberRepository.findMemberByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Location location = member.getLocation();

        Optional<ExtraWeather> searchedExtraWeather = extraWeatherRepository.findById(location.getId());

        if (searchedExtraWeather.isEmpty()){
            WeatherResponse.ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(toLocationDTO(location));

            saveExtraWeatherInfo(location, extraWeatherInfo);

            return extraWeatherInfo;
        }

        ExtraWeather extraWeather = searchedExtraWeather.get();

        if (extraWeather.getBasetime().isBefore(LocalDateTime.now().minusHours(3))){

            WeatherResponse.ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(toLocationDTO(location),extraWeather.getBasetime());
            extraWeather.updateExtraWeather(extraWeatherInfo);

            return extraWeatherInfo;

        } else {
            return WeatherResponse.ExtraWeatherInfo.builder()
                    .baseTime(extraWeather.getBasetime())
                    .uvGrade(extraWeather.getUv())
                    .o3Grade(extraWeather.getO3())
                    .pm10Grade(extraWeather.getPm10())
                    .pm25Grade(extraWeather.getPm25())
                    .build();
        }
    }

    private void saveExtraWeatherInfo(Location location, WeatherResponse.ExtraWeatherInfo extraWeatherInfo) {
        ExtraWeather result = ExtraWeather.builder()
                .location(location)
                .basetime(extraWeatherInfo.getBaseTime())
                .uv(extraWeatherInfo.getUvGrade())
                .o3(extraWeatherInfo.getO3Grade())
                .pm10(extraWeatherInfo.getPm10Grade())
                .pm25(extraWeatherInfo.getPm25Grade())
                .build();

        extraWeatherRepository.save(result);
    }
}
