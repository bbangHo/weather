package org.pknu.weather.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.QueryUtils;
import org.pknu.weather.domain.Location;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.pknu.weather.domain.QLocation.location;
import static org.pknu.weather.domain.QWeather.weather;

@RequiredArgsConstructor
public class WeatherCustomRepositoryImpl implements WeatherCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public void getPrcpProb(Location locationEntity) {
        // Projections.constructor
        LocalDateTime startDt = LocalDateTime.now();
        LocalDateTime endDt = startDt.plusHours(24);

        jpaQueryFactory
                .select(location)
                .from(location)
                .join(location.weatherList, weather).fetchJoin()
                .where(
                        QueryUtils.isContains(locationEntity)
                        
                )
                .fetchOne();
    }

    /**
     * 해당 지역의 날씨가 갱신되었는지 확인하는 메서드
     * @param location
     * @return true = 갱신되었음(3시간 안지남), false = 갱신되지 않았음(3시간 지남)
     */
    @Override
    public boolean weatherHasBeenUpdated(Location location) {
        LocalDateTime now = LocalDateTime.now();
        now = now.withMinute(0);
        now = now.withSecond(0);
        now = now.withNano(0);
        LocalTime baseTime = DateTimeFormatter.getTimeClosestToPresent(now.toLocalTime());

        return jpaQueryFactory
                .select(weather)
                .from(weather)
                .where(
                        weather.presentationTime.after(now),
                        weather.basetime.lt(LocalDateTime.of(now.toLocalDate(), baseTime)),
                        weather.location.eq(location)
                )
                .fetchOne() == null;
    }

    /**
     * 해당 지역의 날씨 데이터가 존재하는지 확인하는 메서드
     * @param location
     * @return true = 존재함, false = 존재 하지 않음
     */
    @Override
    public boolean weatherHasBeenCreated(Location location) {
        LocalDateTime now = LocalDateTime.now();
        now = now.withMinute(0);
        now = now.withSecond(0);
        now = now.withNano(0);

        return jpaQueryFactory
                .select(weather)
                .from(weather)
                .where(
                        weather.presentationTime.after(now),
                        weather.location.eq(location)
                )
                .fetchOne() != null;
    }
}
