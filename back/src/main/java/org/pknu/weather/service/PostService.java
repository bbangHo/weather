package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.*;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.converter.PostConverter;
import org.pknu.weather.dto.converter.RecommendationConverter;
import org.pknu.weather.dto.converter.TagConverter;
import org.pknu.weather.exception.GeneralException;
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
        Tag tag = TagConverter.toTag(createPost);
        Post post = PostConverter.toPost(member, location, tag, createPost);
//        Recommendation recommendation = RecommendationConverter.toRecommendation(member, post);

//        recommendationRepository.save(recommendation);
        postRepository.save(post);
        tagRepository.save(tag);

        return true;
    }

    public boolean createHobbyPost(String email, PostRequest.HobbyParams params) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = locationRepository.safeFindById(params.getLocationId());
        Post post = PostConverter.toPost(member, location, params);
        postRepository.save(post);
        return true;
    }

    @Transactional
    public boolean addRecommendation(Long memberId, Long postId) {
        Boolean isRecommended = recommendationRepository.isRecommended(memberId, postId);

        if (!isRecommended) {
            throw new GeneralException(ErrorStatus._RECOMMENDATION_BAD_REQUEST);
        }

        Member member = memberRepository.safeFindById(memberId);
        Post post = postRepository.safeFindById(postId);
        Recommendation recommendation = RecommendationConverter.toRecommendation(member, post);

        recommendationRepository.save(recommendation);

        return true;
    }
}
