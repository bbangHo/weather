package org.pknu.weather.common.utils;

import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.Expressions;
import org.pknu.weather.common.GlobalParams;
import org.pknu.weather.domain.Location;

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

        return Expressions.booleanTemplate(expression, GlobalParams.DISTANCE);
    }
}
