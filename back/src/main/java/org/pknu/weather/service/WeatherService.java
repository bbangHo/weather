package org.pknu.weather.service;

import static org.pknu.weather.dto.converter.LocationConverter.toLocationDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.common.WeatherParamsFactory;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherApiResponse;
import org.pknu.weather.dto.WeatherResponse;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.feignClient.WeatherFeignClient;
import org.pknu.weather.feignClient.dto.PointDTO;
import org.pknu.weather.feignClient.dto.WeatherParams;
import org.pknu.weather.feignClient.utils.ExtraWeatherApiUtils;
import org.pknu.weather.feignClient.utils.WeatherApiUtils;
import org.pknu.weather.repository.ExtraWeatherRepository;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("${api.weather.service-key}")
    private String weatherServiceKey;

    /**
     * 사용자의 위도 경도 및 기타 정보를 받아와 Point(x, y)로 치환하고 weather로 반환한다.
     *
     * @return now ~ 24 시간의 Wether 엔티티를 담고있는 List
     * @Location 사용자 위치 엔티티
     */
    public List<Weather> getVillageShortTermForecast(Location location) {
        float lon = location.getLongitude().floatValue();
        float lat = location.getLatitude().floatValue();

        PointDTO pointDTO = GeometryUtils.coordinateToPoint(lon, lat);
        String date = DateTimeFormatter.getFormattedDate();
        String time = DateTimeFormatter.getFormattedTimeByThreeHour();

        WeatherParams weatherParams = WeatherParamsFactory.create(weatherServiceKey, date, time, pointDTO);

        WeatherApiResponse weatherApiResponse = weatherFeignClient.getVillageShortTermForecast(weatherParams);
        List<WeatherApiResponse.Response.Body.Items.Item> itemList = weatherApiResponse.getResponse()
                .getBody()
                .getItems()
                .getItemList();

        return WeatherApiUtils.responseProcess(itemList, date, time);
    }

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
        List<Weather> values = getVillageShortTermForecast(location);
        List<Weather> weatherList = new ArrayList<>(values);

        weatherList.forEach(w -> w.addLocation(location));
        return weatherRepository.saveAll(weatherList);
    }

    @Async("threadPoolTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveWeatherSynchronization(Location loc, List<Weather> forecast) {
        Location location = locationRepository.findById(loc.getId()).get();
        float lon = location.getLongitude().floatValue();
        float lat = location.getLatitude().floatValue();

        List<Weather> weatherList = new ArrayList<>(forecast);

        weatherList.forEach(w -> w.addLocation(location));
        weatherRepository.saveAll(weatherList);
    }

    /**
     * 단기 날씨 예보 API가 3시간 마다 갱신되기 때문에, 날씨 데이터 갱신을 위한 메서드
     *
     * @param loc API를 호출한 사용자의 Location 엔티티
     * @return 해당 위치의 날씨 데이터 List
     */
    @Async("threadPoolTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateWeathers(Location loc) {
        Location location = locationRepository.safeFindById(loc.getId());
        weatherRepository.deleteAllByLocation(location);

        List<Weather> newWeathers = getVillageShortTermForecast(location).stream()
                .toList();

        newWeathers.forEach(weather -> {
            weather.addLocation(location);
        });

        weatherRepository.saveAll(newWeathers);
    }

    /**
     * 예보 시간이 현재 보다 과거이면 모두 삭제합니다.v
     */
    @Async("threadPoolDeleteTaskExecutor")
    public void bulkDeletePastWeather() {
        weatherRepository.bulkDeletePastWeathers();
    }

    @Transactional
    public WeatherResponse.ExtraWeatherInfo extraWeatherInfo(String email, Long locationId) {

        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Location location;

        if (locationId != null) {
            location = locationRepository.safeFindById(locationId);
        } else {
            location = member.getLocation();
        }

        Optional<ExtraWeather> searchedExtraWeather = extraWeatherRepository.findByLocationId(location.getId());

        if (searchedExtraWeather.isEmpty()) {
            WeatherResponse.ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(
                    toLocationDTO(location));

            saveExtraWeatherInfo(location, extraWeatherInfo);

            return extraWeatherInfo;
        }

        ExtraWeather extraWeather = searchedExtraWeather.get();

        if (extraWeather.getBasetime().isBefore(LocalDateTime.now().minusHours(3))) {

            WeatherResponse.ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(
                    toLocationDTO(location), extraWeather.getBasetime());
            extraWeather.updateExtraWeather(extraWeatherInfo);

            return extraWeatherInfo;

        } else {
            return transferToExtraWeatherInfo(extraWeather);
        }
    }

    private WeatherResponse.ExtraWeatherInfo transferToExtraWeatherInfo(ExtraWeather extraWeather) {
        return WeatherResponse.ExtraWeatherInfo.builder()
                .baseTime(extraWeather.getBasetime())
                .uvGrade(extraWeather.getUv())
                .uvGradePlus3(extraWeather.getUvPlus3())
                .uvGradePlus6(extraWeather.getUvPlus6())
                .uvGradePlus9(extraWeather.getUvPlus9())
                .uvGradePlus12(extraWeather.getUvPlus12())
                .uvGradePlus15(extraWeather.getUvPlus15())
                .uvGradePlus18(extraWeather.getUvPlus18())
                .uvGradePlus21(extraWeather.getUvPlus21())
                .o3Grade(extraWeather.getO3())
                .pm10Grade(extraWeather.getPm10())
                .pm25Grade(extraWeather.getPm25())
                .build();
    }

    private void saveExtraWeatherInfo(Location location, WeatherResponse.ExtraWeatherInfo extraWeatherInfo) {
        ExtraWeather result = ExtraWeather.builder()
                .location(location)
                .basetime(extraWeatherInfo.getBaseTime())
                .uv(extraWeatherInfo.getUvGrade())
                .uvPlus3(extraWeatherInfo.getUvGradePlus3())
                .uvPlus6(extraWeatherInfo.getUvGradePlus6())
                .uvPlus9(extraWeatherInfo.getUvGradePlus9())
                .uvPlus12(extraWeatherInfo.getUvGradePlus12())
                .uvPlus15(extraWeatherInfo.getUvGradePlus15())
                .uvPlus18(extraWeatherInfo.getUvGradePlus18())
                .uvPlus21(extraWeatherInfo.getUvGradePlus21())
                .o3(extraWeatherInfo.getO3Grade())
                .pm10(extraWeatherInfo.getPm10Grade())
                .pm25(extraWeatherInfo.getPm25Grade())
                .build();

        extraWeatherRepository.save(result);
    }
}
