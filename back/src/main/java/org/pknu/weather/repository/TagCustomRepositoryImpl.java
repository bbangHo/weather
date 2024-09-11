package org.pknu.weather.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.GlobalParams;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.dto.TagQueryResult;

import java.util.ArrayList;
import java.util.List;

import static org.pknu.weather.domain.QLocation.location;
import static org.pknu.weather.domain.QTag.tag;

@RequiredArgsConstructor
public class TagCustomRepositoryImpl implements TagCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<TagQueryResult> rankingTags(Location locationEntity) {
        List<TagQueryResult> tagQueryResultList = new ArrayList<>();

        tagQueryResultList.add(getTagTuple(locationEntity, tag.temperTag));
        tagQueryResultList.add(getTagTuple(locationEntity, tag.windTag));
        tagQueryResultList.add(getTagTuple(locationEntity, tag.humidityTag));
        tagQueryResultList.add(getTagTuple(locationEntity, tag.skyTag));
        tagQueryResultList.add(getTagTuple(locationEntity, tag.dustTag));

        return tagQueryResultList.stream()
                .sorted((o1, o2) -> Math.toIntExact(o1.getCount() - o2.getCount()))
                .toList();
    }

    private TagQueryResult getTagTuple(Location locationEntity, EnumPath<? extends EnumTag> pTag) {
        Tuple tuple = jpaQueryFactory
                .select(pTag.count(), pTag)
                .from(tag)
                .join(tag.location, location)
                .where(
                        isContains(locationEntity)
                )
                .groupBy(pTag)
                .orderBy(pTag.count().desc())
                .fetchFirst();

        assert tuple != null;
        return TagQueryResult.builder()
                .tag(tuple.get(pTag))
                .count(tuple.get(pTag.count()))
                .build();
    }

    private BooleanTemplate isContains(Location locationEntity) {
        Double latitude = locationEntity.getLatitude();
        Double longitude = locationEntity.getLongitude();

        String target = "Point(%f %f)".formatted(latitude, longitude);
        String geoFunction = "ST_CONTAINS(ST_BUFFER(ST_GeomFromText('%s', 4326), {0}), point)";
        String expression = String.format(geoFunction, target);

        return Expressions.booleanTemplate(expression, GlobalParams.DISTANCE);
    }
}
