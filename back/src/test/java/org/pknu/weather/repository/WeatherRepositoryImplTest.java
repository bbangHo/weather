package org.pknu.weather.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestGlobalParams;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
class WeatherRepositoryImplTest {
    @Autowired
    WeatherRepository weatherRepository;

    @Test
    void 날씨_갱신_검증_로직_테스트() {
        // given
        Location location = Location.builder()
                .point(GeometryUtils.getPoint(TestGlobalParams.LATITUDE, TestGlobalParams.LONGITUDE))
                .city("city")
                .province("province")
                .street("street")
                .latitude(30.0)
                .longitude(60.0)
                .build();

        // when
        boolean result = weatherRepository.weatherHasBeenUpdated(location);

        // then
    }


}