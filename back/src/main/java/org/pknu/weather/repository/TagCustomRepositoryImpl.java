package org.pknu.weather.repository;

import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.GlobalConstant;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.QTag;
import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.dto.QTagQueryResult;
import org.pknu.weather.dto.TagQueryResult;

import java.util.Arrays;

import static org.pknu.weather.domain.QLocation.location;
import static org.pknu.weather.domain.QTag.tag;

@RequiredArgsConstructor
public class TagCustomRepositoryImpl implements TagCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public TagQueryResult rankingTags(Location locationEntity) {
        EnumTag temperTag = generateMostTagQuery(locationEntity, tag.temperTag);
        EnumTag windTag = generateMostTagQuery(locationEntity, tag.windTag);
        EnumTag humidityTag = generateMostTagQuery(locationEntity, tag.humidityTag);
        EnumTag skyTag = generateMostTagQuery(locationEntity, tag.skyTag);
        EnumTag dustTag = generateMostTagQuery(locationEntity, tag.dustTag);
    }

    private <T extends Enum<T>> EnumTag generateMostTagQuery(Location locationEntity, EnumPath<T> qTag) {
        return jpaQueryFactory
                .select(qTag)
                .from(tag)
                .join(tag.location, location)
                .where(
                        isContains(locationEntity)
                )
                .groupBy(qTag)
                .orderBy(qTag.count().desc())
                .fetchOne();
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
