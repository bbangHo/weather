package org.pknu.weather.repository;

import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long>, RecommendationCustomRepository {

    void deleteByMemberAndPostId(Member member, Long postId);
}
