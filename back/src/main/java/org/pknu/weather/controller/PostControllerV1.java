package org.pknu.weather.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.common.converter.TokenConverter;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.converter.PostRequestConverter;
import org.pknu.weather.service.PostQueryService;
import org.pknu.weather.service.PostService;
import org.pknu.weather.validation.annotation.IsPositive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class PostControllerV1 {
    private final PostRequestConverter postRequestConverter;
    private final PostService postService;
    private final PostQueryService postQueryService;

    @PostMapping("/post")
    public ApiResponse<Object> createWeatherPost(@RequestHeader("Authorization") String authorization,
                                                 @Valid @RequestBody PostRequest.Params params) {
        PostRequest.CreatePost createPost = postRequestConverter.toCreatePost(params);
        String email = TokenConverter.getEmailByToken(authorization);
        boolean isSuccess = postService.createWeatherPost(email, createPost);
        return ApiResponse.of(isSuccess);
    }

    @PostMapping("/post/hobby")
    public ApiResponse<Object> createHobbyPost(@RequestHeader("Authorization") String authorization,
                                               @RequestBody PostRequest.HobbyParams params) {
        String email = TokenConverter.getEmailByToken(authorization);
        boolean isSuccess = postService.createHobbyPost(email, params);
        return ApiResponse.of(isSuccess);
    }

    @PostMapping("/post/recommendation")
    public ApiResponse<Object> addLike(Long memberId, @IsPositive Long postId) {
        boolean result = postService.addRecommendation(memberId, postId);
        return ApiResponse.of(result);
    }
}
