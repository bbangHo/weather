package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.dto.PostResponse;
import org.pknu.weather.service.PostQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
public class CommunityControllerV1 {
    private final PostQueryService postQueryService;

    @GetMapping("/posts")
    public ApiResponse<PostResponse.PostList> getPosts(Long memberId,
                                                       @RequestParam(defaultValue = "1") Long lastPostId,
                                                       @RequestParam(defaultValue = "6") Long size,
                                                       @RequestParam(defaultValue = "WEATHER") String postType,
                                                       @RequestParam(defaultValue = "0") Long locationId) {
        PostResponse.PostList postList = postQueryService.getWeatherPosts(memberId, lastPostId, size, postType, locationId);
        return ApiResponse.onSuccess(postList);
    }
}
