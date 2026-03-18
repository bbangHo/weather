package org.pknu.weather.weather.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pknu.weather.config.EmbeddedRedisConfig;
import org.pknu.weather.weather.dto.WeatherRedisDTO;
import org.pknu.weather.weather.enums.RainType;
import org.pknu.weather.weather.enums.SkyType;
import org.pknu.weather.weather.utils.WeatherRedisConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.pknu.weather.weather.utils.WeatherRedisKeyUtils.buildKey;

@SpringBootTest
@Import({EmbeddedRedisConfig.class})
class WeatherRedisRepositoryTest {

    // 🔥 1. RedisTemplate 대신 StringRedisTemplate으로 변경
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 🔥 2. JSON 파싱 검증을 위해 Converter 주입
    @Autowired
    private WeatherRedisConverter weatherRedisConverter;

    @Autowired
    private WeatherRedisRepository weatherRedisRepository;

    private final Long locationId = 1L;
    private final LocalDateTime now = LocalDateTime.of(2025, 1, 1, 2, 0);
    private List<WeatherRedisDTO.WeatherData> weatherList;

    @BeforeEach
    void clearRedis() {
        // 기존 객체 템플릿의 커넥션이 아닌 String 템플릿의 커넥션을 사용해 Flush
        Objects.requireNonNull(stringRedisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushAll();

        weatherList = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            LocalDateTime presentationTime = now.plusHours(i);
            weatherList.add(WeatherRedisDTO.WeatherData.builder()
                    .presentationTime(presentationTime)
                    .basetime(now)
                    .windSpeed(3.5)
                    .humidity(60)
                    .rainProb(40)
                    .rain(0.0f)
                    .rainType(RainType.NONE)
                    .temperature(28)
                    .sensibleTemperature(30.0)
                    .snowCover(0.0f)
                    .skyType(SkyType.CLEAR)
                    .build());
        }

        for (int i = 24; i < 27; i++) {
            LocalDateTime presentationTime = now.plusHours(i);
            weatherList.add(WeatherRedisDTO.WeatherData.builder()
                    .presentationTime(presentationTime)
                    .basetime(now.plusHours(3))
                    .windSpeed(3.5)
                    .humidity(60)
                    .rainProb(40)
                    .rain(0.0f)
                    .rainType(RainType.NONE)
                    .temperature(28)
                    .sensibleTemperature(30.0)
                    .snowCover(0.0f)
                    .skyType(SkyType.CLEAR)
                    .build());
        }
    }

    @Test
    @DisplayName("getWeather: 24시간 분량의 데이터를 정상적으로 반환한다")
    void getWeather_반환성공테스트() {
        // given
        for (int i = 0; i < 24; i++) {
            weatherRedisRepository.saveWeather(locationId, weatherList.get(i), Duration.ofHours(24));
        }

        // when
        List<WeatherRedisDTO.WeatherData> weathers = weatherRedisRepository.multiGetWeathers(locationId, now);

        // then
        assertThat(weathers).isNotNull();
        assertThat(weathers.size()).isEqualTo(24);
    }

    @Test
    @DisplayName("getWeather: 저장된 데이터가 없으면 빈 리스트를 반환한다")
    void getWeather_null반환테스트() {
        // when
        List<WeatherRedisDTO.WeatherData> weathers = weatherRedisRepository.multiGetWeathers(locationId, now);

        // then
        assertThat(weathers).isNotNull();
        assertThat(weathers.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("saveWeather: 순수 JSON 문자열 형태로 Redis에 정상 저장된다")
    void saveWeather_정상저장() {
        // given
        String key = buildKey(locationId, now);
        weatherRedisRepository.saveWeather(locationId, weatherList.get(0), Duration.ofHours(24));

        // when
        String jsonResult = stringRedisTemplate.opsForValue().get(key);

        // then
        assertThat(jsonResult).isNotNull();
        // Converter를 통해 다시 객체로 살려내어 값이 맞는지 검증합니다.
        WeatherRedisDTO.WeatherData parsedResult = weatherRedisConverter.fromJson(jsonResult);
        assertThat(parsedResult.getTemperature()).isEqualTo(28);
    }

    @Test
    @DisplayName("updateWeather: 기존 값을 새로운 JSON 데이터로 덮어쓴다")
    void updateWeather_기존값덮어쓰기() {
        // given
        String key = buildKey(locationId, now);
        Integer pastTemperature = 35;
        WeatherRedisDTO.WeatherData updatedData = WeatherRedisDTO.WeatherData.builder()
                .presentationTime(now)
                .basetime(now)
                .windSpeed(3.5)
                .humidity(60)
                .rainProb(40)
                .rain(0.0f)
                .rainType(RainType.NONE)
                .temperature(pastTemperature)
                .sensibleTemperature(30.0)
                .snowCover(0.0f)
                .skyType(SkyType.CLEAR)
                .build();

        weatherRedisRepository.saveWeather(locationId, weatherList.get(0), Duration.ofHours(24));
        weatherRedisRepository.updateWeather(locationId, updatedData);

        // when
        String jsonResult = stringRedisTemplate.opsForValue().get(key);
        WeatherRedisDTO.WeatherData parsedResult = weatherRedisConverter.fromJson(jsonResult);

        // then
        assertThat(parsedResult.getTemperature()).isEqualTo(35);
    }

    @Test
    @DisplayName("expireWeather: 지정된 TTL이 정상적으로 적용된다")
    void expireWeather_TTL_적용됨() {
        // given
        weatherRedisRepository.saveWeather(locationId, weatherList.get(0), Duration.ofHours(21));

        // when
        weatherRedisRepository.expireWeather(locationId, now, 24); // 24시간으로 갱신

        // then
        Long ttl = stringRedisTemplate.getExpire(buildKey(locationId, now), TimeUnit.HOURS);
        assertThat(ttl).isNotNull();
        assertThat(ttl).isLessThanOrEqualTo(24);
        assertThat(ttl).isGreaterThan(0); // 만료되지 않았음을 확인
    }

    @Test
    @DisplayName("deleteValues: 데이터가 정상적으로 삭제된다")
    void deleteValues_정상삭제() {
        // given
        weatherRedisRepository.saveWeather(locationId, weatherList.get(0), Duration.ofHours(24));

        // when
        weatherRedisRepository.deleteValues(locationId, now);

        // then
        String result = stringRedisTemplate.opsForValue().get(buildKey(locationId, now));
        assertThat(result).isNull(); // StringRedisTemplate은 값이 없으면 null을 반환합니다.
    }
}