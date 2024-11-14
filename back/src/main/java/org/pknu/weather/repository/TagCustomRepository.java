package org.pknu.weather.repository;

import org.pknu.weather.domain.Location;
import org.pknu.weather.dto.TagQueryResult;

import java.util.List;

public interface TagCustomRepository {
    List<TagQueryResult> rankingTags(Location locationEntity);
}
