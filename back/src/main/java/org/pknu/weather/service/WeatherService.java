package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.dto.LocationDTO;
import org.pknu.weather.dto.WeatherResponse;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.feignClient.AirConditionClient;
import org.pknu.weather.feignClient.UVClient;
import org.pknu.weather.feignClient.WeatherFeignClient;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.feignClient.utils.ExtraWeatherApiUtils;
import org.pknu.weather.repository.ExtraWeatherRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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

    public List<Weather> getWeathers(Member member) {
        Location location = member.getLocation();
        return weatherRepository.findAllWithLocation(location, LocalDateTime.now().plusHours(24)).stream()
                .sorted(Comparator.comparing(Weather::getPresentationTime))
                .toList();
    }

    /**
     * 위도와 경도에 해당하는 지역(읍면동)의 24시간치 날씨 단기 예보 정보를 저장합니다.
     * @param memberId
     * @param lon 경도
     * @param lat 위도
     * @return 위도와 경도에 해당하는 Location의 Weather list를 반환
     */
    @Transactional
    public List<Weather> saveWeathers(Long memberId, Float lon, Float lat) {
        log.debug("%logger{0}, %M, memberId: {}, lon: {}, lat: {}", memberId, lon, lat);

        Member member = memberRepository.safeFindById(memberId);
        Location location = member.getLocation();
        ArrayList<Weather> weatherList = new ArrayList<>(weatherFeignClient.preprocess(lon, lat).values());

        weatherList.forEach(w -> w.addLocation(location));
        return weatherRepository.saveAll(weatherList);
    }

    /**
     * 예보 시간이 현재 보다 과거이면 모두 삭제합니다.
     */
    public void bulkDeletePastWeather() {
        weatherRepository.bulkDeletePastWeathers();
    }

    @Transactional
    public WeatherResponse.ExtraWeatherInfo extraWeatherInfo(String email){

        Member member = memberRepository.findMemberByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Location location = member.getLocation();

        Optional<ExtraWeather> searchedExtraWeather = extraWeatherRepository.findById(location.getId());

        if(searchedExtraWeather.isEmpty()){
            WeatherResponse.ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(toLocationDTO(location));

            ExtraWeather result = ExtraWeather.builder()
                    .location(location)
                    .basetime(extraWeatherInfo.getBaseTime())
                    .uv(extraWeatherInfo.getUvGrade())
                    .o3(extraWeatherInfo.getO3Grade())
                    .pm10(extraWeatherInfo.getPm10Grade())
                    .pm25(extraWeatherInfo.getPm25Grade())
                    .build();
            extraWeatherRepository.save(result);

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
}
