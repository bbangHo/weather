package org.pknu.weather.repository;

import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Post;

import java.util.List;

public interface PostCustomRepository {
    List<Post> findAllWithinDistance(Long lastPostId, Long size, Location location, Integer distance);
}
