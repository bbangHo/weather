package org.pknu.weather.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LocationCustomRepositoryImpl implements LocationCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
}
