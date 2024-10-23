package org.pknu.weather.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Recommendation;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.converter.PostConverter;
import org.pknu.weather.dto.converter.RecommendationConverter;
import org.pknu.weather.dto.converter.TagConverter;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.repository.RecommendationRepository;
import org.pknu.weather.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final RecommendationRepository recommendationRepository;
    private final TagRepository tagRepository;
    private static final int DISTANCE = 3000;

    @Transactional(readOnly = true)
    public List<Post> getPosts(Long memberId, Long lastPostId, Long size, String postType) {
        Member member = memberRepository.safeFindById(memberId);
        Location location = member.getLocation();
        List<Post> postList = postRepository.findAllWithinDistance(lastPostId, size, location,
                PostType.toPostType(postType));

        return postList;
    }

    @Transactional
    public boolean createWeatherPost(Long memberId, PostRequest.CreatePost createPost) {
        Member member = memberRepository.safeFindById(memberId);
        Location location = member.getLocation();
        Tag tag = TagConverter.toTag(createPost);
        Post post = PostConverter.toPost(member, location, tag, createPost);
//        Recommendation recommendation = RecommendationConverter.toRecommendation(member, post);

//        recommendationRepository.save(recommendation);
        postRepository.save(post);
        tagRepository.save(tag);

        return true;
    }

    public boolean createHobbyPost(PostRequest.HobbyParams params) {
        Member member = memberRepository.safeFindById(params.getMemberId());
        Location location = member.getLocation();   // TODO: target location 으로 변경
        Post post = PostConverter.toPost(member, location, null, params);
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
