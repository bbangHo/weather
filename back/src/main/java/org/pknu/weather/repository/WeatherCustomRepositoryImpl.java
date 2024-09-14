package org.pknu.weather.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.utils.QueryUtils;
import org.pknu.weather.domain.Location;

import java.time.LocalDateTime;

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
}
