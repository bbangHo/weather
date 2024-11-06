package org.pknu.weather.service;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Slf4j
class MainPageServiceTest {
    @Autowired
    MainPageService mainPageService;

    @Autowired
    WeatherService weatherService;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WeatherRepository weatherRepository;

    @Test
    @Transactional
    void 사용자_지역이_없을_때_테스트() {
        // given

        // when

        // then
    }
}
