package org.pknu.weather.repository;

import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.GlobalConstant;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.tag.*;
import org.pknu.weather.dto.TagQueryResult;

import static org.pknu.weather.domain.QLocation.location;
import static org.pknu.weather.domain.QTag.tag;

@RequiredArgsConstructor
public class TagCustomRepositoryImpl implements TagCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public TagQueryResult rankingTags(Location locationEntity) {
        return TagQueryResult.builder()
                .tempCount(getTempTagCount(locationEntity))
                .windCount(getWindTagCount(locationEntity))
                .humidityCount(getHumidityTagCount(locationEntity))
                .skyCount(getSkyTagCount(locationEntity))
                .dustCount(getDustTagCount(locationEntity))
                .build();
    }

    private TemperatureTag getTempTagCount(Location locationEntity) {
        return jpaQueryFactory
                .select(tag.temperTag)
                .from(tag)
                .join(tag.location, location)
                .where(
                        isContains(locationEntity)
                )
                .groupBy(tag.temperTag)
                .orderBy(tag.temperTag.count().desc())
                .fetchFirst();
    }

    private WindTag getWindTagCount(Location locationEntity) {
        return jpaQueryFactory
                .select(tag.windTag)
                .from(tag)
                .join(tag.location, location)
                .where(
                        isContains(locationEntity)
                )
                .groupBy(tag.windTag)
                .orderBy(tag.windTag.count().desc())
                .fetchFirst();
    }

    private HumidityTag getHumidityTagCount(Location locationEntity) {
        return jpaQueryFactory
                .select(tag.humidityTag)
                .from(tag)
                .join(tag.location, location)
                .where(
                        isContains(locationEntity)
                )
                .groupBy(tag.humidityTag)
                .orderBy(tag.humidityTag.count().desc())
                .fetchFirst();
    }

    private SkyTag getSkyTagCount(Location locationEntity) {
        return jpaQueryFactory
                .select(tag.skyTag)
                .from(tag)
                .join(tag.location, location)
                .where(
                        isContains(locationEntity)
                )
                .groupBy(tag.skyTag)
                .orderBy(tag.skyTag.count().desc())
                .fetchFirst();
    }

    private DustTag getDustTagCount(Location locationEntity) {
        return jpaQueryFactory
                .select(tag.dustTag)
                .from(tag)
                .join(tag.location, location)
                .where(
                        isContains(locationEntity)
                )
                .groupBy(tag.dustTag)
                .orderBy(tag.dustTag.count().desc())
                .fetchFirst();
    }

    private BooleanTemplate isContains(Location locationEntity) {
        Double latitude = locationEntity.getLatitude();
        Double longitude = locationEntity.getLongitude();

        String target = "Point(%f %f)".formatted(latitude, longitude);
        String geoFunction = "ST_CONTAINS(ST_BUFFER(ST_GeomFromText('%s', 4326), {0}), point)";
        String expression = String.format(geoFunction, target);

        return Expressions.booleanTemplate(expression, GlobalConstant.DISTANCE);
    }
}
