package org.pknu.weather.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.pknu.weather.domain.Location;
import org.pknu.weather.dto.TagQueryResult;
import org.pknu.weather.preview.dto.Response.TagHour;

public interface TagCustomRepository {
    List<TagQueryResult> rankingTags(Location locationEntity);

    // 시각화 페이지용 메서드
    List<TagHour> countTemperTagsForHour(Location location, LocalDateTime startTime, Integer sensitivity);
}
