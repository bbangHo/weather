package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.dto.PostResponse;
import org.pknu.weather.dto.TagDto;
import org.pknu.weather.dto.WeatherResponse;
import org.pknu.weather.service.MainPageService;
import org.pknu.weather.service.WeatherService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;

/**
 * 메인 화면에 사용되는 API를 관리하는 컨트롤러. 화면용입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainPageControllerV1 {
    private final MainPageService mainPageService;
    private final WeatherService weatherService;

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

    @GetMapping(value = "/extraWeatherInfo")
    public ApiResponse<WeatherResponse.ExtraWeatherInfo> getExtraWeatherInfo(@RequestHeader("Authorization") String authorization) {

        String email = getEmailByToken(authorization);
        WeatherResponse.ExtraWeatherInfo extraWeatherInfo = weatherService.extraWeatherInfo(email);

        return ApiResponse.onSuccess(extraWeatherInfo);
    }

      @GetMapping("/weather/simple/tags")
    public ApiResponse<List<TagDto.SimpleTag>> getMostSelectedTags(/*@RequestHeader("Authorization") String authorization*/@RequestParam Long memberId) {
//        String email = TokenConverter.getEmailByToken(authorization);
        List<TagDto.SimpleTag> mostTags = mainPageService.getMostSelectedTags(memberId);
        return ApiResponse.onSuccess(mostTags);
    }

    @GetMapping("/weather/simple/rain")
    public ApiResponse<Object> getRainProbability(@RequestParam Long memberId) {
        WeatherResponse.SimpleRainInformation rainProb = mainPageService.getSimpleRainInfo(memberId);
        return ApiResponse.onSuccess(rainProb);
    }
}
