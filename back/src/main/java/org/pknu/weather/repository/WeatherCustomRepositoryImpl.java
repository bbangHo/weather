package org.pknu.weather.repository;

import static org.pknu.weather.domain.QWeather.weather;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.QueryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.QLocation;
import org.pknu.weather.domain.QWeather;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.common.RainType;
import org.pknu.weather.dto.WeatherQueryResult;
import org.pknu.weather.test.alarm.dto.WeatherSummaryDTO;


@RequiredArgsConstructor
public class WeatherCustomRepositoryImpl implements WeatherCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;


    /**
     * 24시간 이내에 비소식이 있는지 확인합니다.
     *
     * @param locationEntity
     * @return 비소식이 없으면 null을 반환.
     */
    public WeatherQueryResult.SimpleRainInfo getSimpleRainInfo(Location locationEntity) {
        return jpaQueryFactory
                .select(Projections.constructor(WeatherQueryResult.SimpleRainInfo.class,
                        weather.presentationTime,
                        weather.rainProb,
                        weather.rain,
                        weather.snowCover
                ))
                .from(weather)
                .where(
                        QueryUtils.isSameLocation(locationEntity, weather),
                        QueryUtils.presentationTimeWithinLast24Hours(weather),
                        weather.rain.gt(0).or(weather.snowCover.gt(0))
                )
                .fetchFirst();
    }

    /**
     * 해당 지역의 날씨가 갱신되었는지 확인하는 메서드 ex. baseTime: 14:00, now: 14:00~16:59 true baseTime: 14:00, now: 17:00~      false
     *
     * @param location
     * @return true = 갱신되었음(3시간 안지남), false = 갱신되지 않았음(3시간 지남)
     */
    @Override
    public boolean weatherHasBeenUpdated(Location location) {
        LocalDateTime now = LocalDateTime.now()
                .withMinute(15)
                .withSecond(0)
                .withNano(0);

        LocalDateTime baseTime = DateTimeFormatter.getBaseLocalDateTime();

        LocalDateTime weatherBaseTime = jpaQueryFactory
                .select(weather.basetime)
                .from(weather)
                .where(
                        weather.presentationTime.after(now),
                        weather.location.eq(location)
                )
                .fetchFirst();

        return weatherBaseTime != null && weatherBaseTime.isEqual(baseTime);
    }

    /**
     * 해당 지역의 날씨 데이터가 존재하는지 확인하는 메서드
     *
     * @param location
     * @return true = 존재함, false = 존재 하지 않음
     */
    @Override
    public boolean weatherHasBeenCreated(Location location) {
        LocalDateTime now = LocalDateTime.now()
                .withMinute(15)
                .withSecond(0)
                .withNano(0);

        Weather w = jpaQueryFactory
                .select(weather)
                .from(weather)
                .where(
                        weather.presentationTime.after(now),
                        weather.location.eq(location)
                )
                .fetchFirst();

        return w != null;
    }

    @Override
    public Weather findByLocationClosePresentationTime(Location location) {
        LocalDateTime now = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0);

        return jpaQueryFactory
                .selectFrom(weather)
                .where(
                        weather.location.eq(location),
                        weather.presentationTime.eq(now)
                )
                .fetchOne();
    }

    /**
     * 특정 지역의 날씨 예보중 현재 시각 이후의 예보들을 가져온다.
     *
     * @param locationEntity
     */
    @Override
    public Map<LocalDateTime, Weather> findAllByLocationAfterNow(Location locationEntity) {
        LocalDateTime now = LocalDateTime.now();

        List<Weather> weatherList = jpaQueryFactory
                .select(weather)
                .from(weather)
                .where(
                        weather.location.id.eq(locationEntity.getId()),
                        weather.presentationTime.after(now)
                )
                .fetch();

        return weatherList.stream()
                .collect((Collectors.toMap(Weather::getPresentationTime,
                        weather -> weather,
                        (existing, replacement) -> existing)));
    }

    @Override
    public List<WeatherSummaryDTO> findWeatherSummary(Set<Long> locationIds) {

        QWeather weather = QWeather.weather;
        QLocation location = QLocation.location;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDateTime = now.toLocalDate().plusDays(1).atStartOfDay();

        BooleanExpression isRainOrShower = weather.rainType.in(RainType.RAIN, RainType.SHOWER);
        BooleanExpression isSnow = weather.rainType.eq(RainType.SNOW);
        BooleanExpression isRainAndSnow = weather.rainType.eq(RainType.RAIN_AND_SNOW);
        BooleanExpression isNotNone = weather.rainType.ne(RainType.NONE);

        NumberTemplate<Integer> rainCount = Expressions.numberTemplate(Integer.class, "SUM(CASE WHEN {0} THEN 1 ELSE 0 END)", isRainOrShower);
        NumberTemplate<Integer> snowCount = Expressions.numberTemplate(Integer.class, "SUM(CASE WHEN {0} THEN 1 ELSE 0 END)", isSnow);
        NumberTemplate<Integer> rainAndSnowCount = Expressions.numberTemplate(Integer.class, "SUM(CASE WHEN {0} THEN 1 ELSE 0 END)", isRainAndSnow);
        NumberTemplate<Integer> notNoneCount = Expressions.numberTemplate(Integer.class, "SUM(CASE WHEN {0} THEN 1 ELSE 0 END)", isNotNone);

        StringExpression rainStatus = new CaseBuilder()
                .when(rainAndSnowCount.gt(0)).then("RAIN_AND_SNOW")
                .when(rainCount.gt(0).and(snowCount.eq(0))).then("RAIN")
                .when(snowCount.gt(0).and(rainCount.eq(0))).then("SNOW")
                .when(notNoneCount.eq(0)).then("NONE")
                .otherwise("RAIN_AND_SNOW");

        return jpaQueryFactory
                .select(Projections.fields(
                        WeatherSummaryDTO.class,
                        location.id.as("locationId"),
                        weather.temperature.max().as("maxTemp"),
                        weather.temperature.min().as("minTemp"),
                        rainStatus.as("rainStatus")
                ))
                .from(weather)
                .join(weather.location, location)
                .where(
                        weather.presentationTime.between(now, endDateTime)
                                .and(location.id.in(locationIds)))
                .groupBy(location.id)
                .fetch();
    }
}
