package org.pknu.weather.repository;

import static org.pknu.weather.domain.QLocation.location;
import static org.pknu.weather.domain.QMember.member;
import static org.pknu.weather.domain.QPost.post;
import static org.pknu.weather.domain.QTag.tag;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.utils.QueryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.dto.TagQueryResult;
import org.pknu.weather.preview.dto.Response.TagHour;

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


    // 시각화 페이지용 메서드
    @Override
    public List<TagHour> countTemperTagsForHour(Location location, LocalDateTime startTime,
                                                Integer sensitivityCode) {
        startTime = startTime.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = startTime.plusHours(59).plusMinutes(59).plusSeconds(59).withNano(0);

        Sensitivity sensitivity;
        List<Tuple> results;

        if (sensitivityCode == 0) {
            results = jpaQueryFactory
                    .select(tag.temperTag, tag.createdAt, tag.temperTag.count())
                    .from(tag)
                    .join(tag.post, post).fetchJoin()
                    .join(post.member, member).fetchJoin()
                    .where(
                            tag.createdAt.between(startTime, endTime),
                            tag.location.eq(location)
                    )
                    .groupBy(tag.temperTag)
                    .fetch();
        } else {
            sensitivity =
                    sensitivityCode == 1 ? Sensitivity.COLD : sensitivityCode == 2 ? Sensitivity.NONE : Sensitivity.HOT;

            results = jpaQueryFactory
                    .select(tag.temperTag, tag.createdAt, tag.temperTag.count())
                    .from(tag)
                    .join(post.tag, tag).fetchJoin()
                    .join(post.member, member).fetchJoin()
                    .where(
                            tag.createdAt.between(startTime, endTime),
                            member.sensitivity.eq(sensitivity),
                            tag.location.eq(location)
                    )
                    .groupBy(tag.temperTag)
                    .fetch();
        }

        return results.stream()
                .map(r -> {
                    return TagHour.builder()
                            .temperatureTag(r.get(0, TemperatureTag.class))
                            .count(r.get(2, Integer.class))
                            .time(r.get(1, LocalDateTime.class).format(DateTimeFormatter.ofPattern("HH")))
                            .build();
                })
                .toList();

    }
}
