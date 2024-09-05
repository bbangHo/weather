package org.pknu.weather.repository;

import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.dto.TagQueryResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagCustomRepository {
    Tag rankingTags(Location locationEntity);
}
