package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.common.converter.TokenConverter;
import org.pknu.weather.dto.PostResponse;
import org.pknu.weather.dto.TagDto;
import org.pknu.weather.dto.WeatherResponse;
import org.pknu.weather.service.MainPageService;
import org.pknu.weather.service.TagQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 메인 화면에 사용되는 API를 관리하는 컨트롤러. 화면용입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainPageControllerV1 {
    private final MainPageService mainPageService;
    private final TagQueryService tagQueryService;

    @GetMapping("/weather")
    public ApiResponse<WeatherResponse.MainPageWeatherData> getMainPageResource(@RequestParam Long memberId) {
        WeatherResponse.MainPageWeatherData weatherInfo = mainPageService.getWeatherInfo(memberId);
        return ApiResponse.onSuccess(weatherInfo);
    }

    @GetMapping("/posts/popular")
    public ApiResponse<List<PostResponse.Post>> getPopularPostList(@RequestParam Long memberId) {
        List<PostResponse.Post> popularPosts = mainPageService.getPopularPosts(memberId);
        return ApiResponse.onSuccess(popularPosts);
    }

    @GetMapping("/weather/simple/tags")
    public ApiResponse<List<TagDto.SimpleTag>> getMostSelectedTags(@RequestHeader("Authorization") String authorization) {
        String email = TokenConverter.getEmailByToken(authorization);
        List<TagDto.SimpleTag> mostTags = tagQueryService.getMostSelectedTags(email);
        return ApiResponse.onSuccess(mostTags);
    }

    @GetMapping("/weather/simple/rainprob")
    public ApiResponse<Object> getRainProbability(@RequestHeader("Authorization") String authorization) {
        String email = TokenConverter.getEmailByToken(authorization);
        return null;
    }
}
