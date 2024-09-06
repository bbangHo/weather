package org.pknu.weather.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.converter.PostRequestConverter;
import org.pknu.weather.service.PostQueryService;
import org.pknu.weather.service.PostService;
import org.pknu.weather.validation.annotation.IsPositive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class PostControllerV1 {
    private final PostRequestConverter postRequestConverter;
    private final PostService postService;
    private final PostQueryService postQueryService;

    @PostMapping("/post")
    public ApiResponse<Object> createPost(Long memberId, @Valid @RequestBody PostRequest.Params params) {
        PostRequest.CreatePost createPost = postRequestConverter.toCreatePost(params);
        boolean isSuccess = postService.createPost(memberId, createPost);
        return ApiResponse.of(isSuccess);
    }

    @PostMapping("/post/recommendation")
    public ApiResponse<Object> addLike(Long memberId, @IsPositive Long postId) {
        boolean result = postService.addRecommendation(memberId, postId);
        return ApiResponse.of(result);
    }
}
