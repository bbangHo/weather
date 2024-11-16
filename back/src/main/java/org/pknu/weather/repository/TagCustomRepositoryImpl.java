package org.pknu.weather.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.utils.QueryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.dto.TagQueryResult;
import org.pknu.weather.preview.dto.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.pknu.weather.domain.QLocation.location;
import static org.pknu.weather.domain.QTag.tag;
import static org.pknu.weather.domain.QPost.post;
import static org.pknu.weather.domain.QMember.member;

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
                        QueryUtils.isContains(locationEntity)
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


    // 시각화 페이지
    public Response.Type countTemperTagsForHour(LocalDateTime startTime, Sensitivity sensitivity) {
        startTime = startTime.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = startTime.plusHours(59).plusMinutes(59).plusSeconds(59).withNano(0);

        List<Tuple> results = jpaQueryFactory
                .select(tag.temperTag, tag.temperTag.count())
                .from(tag)
                .join(post.tag, tag).fetchJoin()
                .join(post.member, member).fetchJoin()
                .where(
                        tag.createdAt.between(startTime, endTime),
                        member.sensitivity.eq(sensitivity)
                )
                .groupBy(tag.temperTag)
                .fetch();

        Map<TemperatureTag, Long> countMap = results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, TemperatureTag.class),
                        tuple -> tuple.get(1, Long.class)
                ));

        return Response.Type.builder()
                .veryCold(countMap.getOrDefault(TemperatureTag.VERY_COLD, 0L).intValue())
                .cold(countMap.getOrDefault(TemperatureTag.COLD, 0L).intValue())
                .littleCold(countMap.getOrDefault(TemperatureTag.LITTLE_COLD, 0L).intValue())
                .cool(countMap.getOrDefault(TemperatureTag.COOL, 0L).intValue())
                .common(countMap.getOrDefault(TemperatureTag.COMMON, 0L).intValue())
                .warm(countMap.getOrDefault(TemperatureTag.WARM, 0L).intValue())
                .littleWarm(countMap.getOrDefault(TemperatureTag.LITTLE_WARM, 0L).intValue())
                .littleHot(countMap.getOrDefault(TemperatureTag.LITTLE_HOT, 0L).intValue())
                .hot(countMap.getOrDefault(TemperatureTag.HOT, 0L).intValue())
                .veryHot(countMap.getOrDefault(TemperatureTag.VERY_HOT, 0L).intValue())
                .build();
    }
}
