package org.pknu.weather.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static org.pknu.weather.domain.QRecommendation.recommendation;

@RequiredArgsConstructor
public class RecommendationCustomRepositoryImpl implements RecommendationCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 사용자가 게시글에 좋아요를 누를 수 있는지 여부를 확인하니다.
     *
     * @param memberId
     * @param postId
     * @return 좋아요를 누를 수 있으면 true, 누를 수 없으면 false
     */
    @Override
    public Boolean isRecommended(Long memberId, Long postId) {
        return jpaQueryFactory
                .selectFrom(recommendation)
                .where(
                        recommendation.post.id.eq(postId),
                        recommendation.member.id.eq(memberId)
                )
                .fetchOne() == null;
    }
}
