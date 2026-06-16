package org.pknu.weather.location.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static org.pknu.weather.weather.QWeather.weather;

@RequiredArgsConstructor
public class LocationCustomRepositoryImpl implements LocationCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;


    /**
     * 최근에 날씨 데이터가 업데이트 된 지역의 Id List를 반환합니다.
     * 자주 갱신되는 상위 n개의 지역에 대해 스케줄 업데이트를 위한 쿼리입니다.
     * @param limitSize
     * @return 지역 id List
     */
    @Override
    public List<Long> findLocationIdsWithRecentlyUpdatedWeather(Integer limitSize) {
        JPAQuery<Long> query = jpaQueryFactory
                .select(weather.location.id)
                .from(weather)
                .groupBy(weather.location.id)
                .orderBy(weather.updatedAt.max().desc());

        if (limitSize != null && limitSize > 0) {
            query.limit(limitSize);
        }

        return query.fetch();
    }
}
