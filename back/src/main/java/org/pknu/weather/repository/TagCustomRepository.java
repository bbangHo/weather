package org.pknu.weather.repository;

import org.pknu.weather.domain.Location;
import org.pknu.weather.dto.TagQueryResult;

public interface TagCustomRepository {
    TagQueryResult rankingTags(Location locationEntity);
}
