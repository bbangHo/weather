package org.pknu.weather.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.QueryUtils;
import org.pknu.weather.domain.Location;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.pknu.weather.domain.QLocation.location;
import static org.pknu.weather.domain.QTag.tag;
import static org.pknu.weather.domain.QWeather.weather;

@RequiredArgsConstructor
public class LocationCustomRepositoryImpl implements LocationCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;


}
