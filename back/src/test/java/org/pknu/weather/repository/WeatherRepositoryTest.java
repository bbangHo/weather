package org.pknu.weather.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestGlobalParams;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@RequiredArgsConstructor
class WeatherRepositoryTest {
    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    LocationRepository locationRepository;

    @Transactional
    void createWeather(LocalDateTime now) {
        Weather weather = Weather.builder()
                .basetime(now)
                .presentationTime(now.plusHours(4))
                .location(createLocation())
                .build();

        weatherRepository.save(weather);
    }

    Location createLocation() {
        Location location = Location.builder()
                .point(GeometryUtils.getPoint(TestGlobalParams.LATITUDE, TestGlobalParams.LONGITUDE))
                .city("city")
                .province("province")
                .street("street")
                .latitude(30.0)
                .longitude(60.0)
                .build();

        return location;
    }


    @Test
    @Transactional
    void 특정_지역의_날씨_갱신_시각이_지나_업데이트_되지_않았다면_False() {
        // given
        LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.now()
                .withMinute(0)
                .withSecond(0)
                .minusHours(3));

        createWeather(now);

        Location location = locationRepository.findAll().get(0);

        // when
        boolean result = weatherRepository.weatherHasBeenUpdated(location);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
    void 특정_지역의_날씨_갱신_시각이_지나_업데이트_되었다면_true() {
        // given
        LocalDateTime now = LocalDateTime.of(LocalDate.now(),
                DateTimeFormatter.getTimeClosestToPresent(LocalDateTime.now().toLocalTime()
                ));

        createWeather(now);

        Location location = locationRepository.findAll().get(0);

        // when
        boolean result = weatherRepository.weatherHasBeenUpdated(location);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @Transactional
    void 특정_지역의_날씨가_등록되지_않았다면_false() {
        // give
        Location location = locationRepository.save(createLocation());

        // when
        boolean result = weatherRepository.weatherHasBeenCreated(location);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
    void 특정_지역의_날씨가_등록되어_있다면_true() {
        // give
        LocalDateTime now = LocalDateTime.of(LocalDate.now(),
                DateTimeFormatter.getTimeClosestToPresent(LocalDateTime.now().toLocalTime()
                ));

        createWeather(now);
        Location location = locationRepository.findAll().get(0);

        // when
        boolean result = weatherRepository.weatherHasBeenCreated(location);

        // then
        assertThat(result).isTrue();
    }


}