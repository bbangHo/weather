package org.pknu.weather.repository;

import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.common.PostType;

import java.util.List;

public interface PostCustomRepository {
    List<Post> findAllWithinDistance(Long lastPostId, Long size, Location location, PostType postType);

    List<Post> getPopularPostList(Location location);

    List<Post> test(Long lastPostId, Long size, Location locationEntity, PostType postType);
}
