package org.pknu.weather.common.utils;

import java.util.List;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Recommendation;

public class RecommendationUtils {

    public static Integer likeCount(List<Recommendation> recommendationList) {
        return recommendationList.size();
    }

    public static Boolean isClickable(List<Recommendation> recommendationList, Member member) {
        return recommendationList.stream()
                .noneMatch(recommendation -> recommendation.getMember().getId().equals(member.getId()));
    }
}
