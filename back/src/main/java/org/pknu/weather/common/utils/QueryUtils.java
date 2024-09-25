package org.pknu.weather.common.utils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.Expressions;
import org.pknu.weather.common.GlobalParams;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.QLocation;
import org.pknu.weather.domain.QWeather;

import java.time.LocalDateTime;

public class QueryUtils {

    /**
     * distance 이내의 거리에 있는 사용자가 작성한 post인지 확인하는 메서드
     *
     * @param locationEntity  위도 (-90 ~ 90) 경도 (-180 ~ 180) 를 가진 엔티티
     * @return BooleanTemplate
     */
    public static BooleanTemplate isContains(Location locationEntity) {
        Double latitude = locationEntity.getLatitude();
        Double longitude = locationEntity.getLongitude();

        String target = "Point(%f %f)".formatted(latitude, longitude);
        String geoFunction = "ST_CONTAINS(ST_BUFFER(ST_GeomFromText('%s', 4326), {0}), point)";
        String expression = String.format(geoFunction, target);

        return Expressions.booleanTemplate(expression, GlobalParams.RADIUS_DISTANCE);
    }

    /**
     * weather의 예보 시간이 24시간 이내인지 검사
     * ex) weather.presentationTIme이 01-01 00:00:00 ~ yyyy-MM-02 00:00:00 사이의 값
     * @param weather
     * @return
     */
    public static BooleanExpression presentationTimeWithinLast24Hours(QWeather weather) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusHours(24);

        return weather.presentationTime.between(now, end);
    }

    public static BooleanExpression isSameLocation(Location location, QWeather weather) {
        return weather.location.eq(location);
    }

}
