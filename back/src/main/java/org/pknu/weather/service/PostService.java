package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.*;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.converter.PostConverter;
import org.pknu.weather.dto.converter.RecommendationConverter;
import org.pknu.weather.dto.converter.TagConverter;
import org.pknu.weather.dto.converter.TagWeatherConverter;
import org.pknu.weather.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final RecommendationRepository recommendationRepository;
    private final TagRepository tagRepository;
    private final LocationRepository locationRepository;
    private final TagWeatherRepository tagWeatherRepository;
    private final WeatherRepository weatherRepository;
    private static final int DISTANCE = 3000;

    @Transactional(readOnly = true)
    public List<Post> getPosts(Long memberId, Long lastPostId, Long size, String postType, Long locationId) {
        Member member = memberRepository.safeFindById(memberId);
        Location location = null;

        if (locationId == 0) {
            location = member.getLocation();
        } else {
            location = locationRepository.safeFindById(locationId);
        }

        List<Post> postList = postRepository.findAllWithinDistance(lastPostId, size, location,
                PostType.toPostType(postType));

        return postList;
    }

    @Transactional
    public boolean createWeatherPost(String email, PostRequest.CreatePost createPost) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = member.getLocation();
        Tag tag = TagConverter.toTag(createPost, location);
        Post post = PostConverter.toPost(member, location, tag, createPost);

        post = postRepository.save(post);
        post.addTag(tag);
        tag = tagRepository.save(tag);

        Weather weather = weatherRepository.findByLocationClosePresentationTime(location);
        TagWeather tagWeather = TagWeatherConverter.toTagWeather(tag, weather);
        tagWeatherRepository.save(tagWeather);

        return true;
    }

    @Transactional
    public boolean createHobbyPost(String email, PostRequest.HobbyParams params) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = locationRepository.safeFindById(params.getLocationId());
        Post post = PostConverter.toPost(member, location, params);
        postRepository.save(post);
        return true;
    }

    @Transactional
    public boolean addRecommendation(String email, Long postId) {
        Member member = memberRepository.safeFindByEmail(email);
        Boolean isRecommended = recommendationRepository.isRecommended(member.getId(), postId);

        if (!isRecommended) {
            recommendationRepository.deleteByMemberAndPostId(member, postId);
            return true;
        }

        Post post = postRepository.safeFindById(postId);
        Recommendation recommendation = RecommendationConverter.toRecommendation(member, post);

        recommendationRepository.save(recommendation);
        return true;
    }
}
