package org.pknu.weather.domain;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.utils.SensibleTemperatureUtils;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
class WeatherTest {
    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    void weather_엔티티는_영속화_되기전_체감_온도를_갱신한다() {
        // given
        Weather weather = Weather.builder()
                .temperature(14)
                .humidity(50)
                .windSpeed(1.5)
                .build();

        // when
        weatherRepository.save(weather);

        // then
        Assertions.assertThat(weather.getSensibleTemperature()).isEqualTo(
                SensibleTemperatureUtils.getSensibleTemperature(weather.getTemperature(), weather.getHumidity(),
                        weather.getWindSpeed()));
    }

    @Test
    @Transactional
    void test() {
        for (int i = -30; i <= 0; i++) {
            for (double w = 0.0f; w <= 30.0f; w += 0.1f) {
                Weather weather = weatherRepository.save(Weather.builder()
                        .temperature(i)
                        .humidity(0)
                        .windSpeed(w)
                        .build());

                em.flush();
                em.clear();

                weather = weatherRepository.findById(weather.getId()).get();

                log.info("온도{}, 체감 {}, 습도 {}, 바람 {}",
                        weather.getTemperature().toString(),
                        weather.getSensibleTemperature().toString(),
                        weather.getHumidity(),
                        weather.getWindSpeed());
            }
        }
    }
}