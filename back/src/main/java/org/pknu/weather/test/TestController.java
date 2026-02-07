package org.pknu.weather.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.ApiResponse;
import org.pknu.weather.common.converter.TokenConverter;
import org.pknu.weather.mainpage.service.MainPageService;
import org.pknu.weather.post.dto.PostResponse;
import org.pknu.weather.post.dto.TagDto;
import org.pknu.weather.weather.dto.WeatherResponseDTO;
import org.pknu.weather.weather.service.WeatherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class TestController {
    private final TestMainPageService TestMainPageService;
    private final MainPageService mainPageService;
    private final WeatherService weatherService;

    @GetMapping("/api/v1/test/main/weather")
    public ApiResponse<WeatherResponseDTO.MainPageWeatherData> getMainPageResourceV1(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) Long locationId) {

        String email = TokenConverter.getEmailByToken(authorization);
        WeatherResponseDTO.MainPageWeatherData weatherInfo = TestMainPageService.getWeatherInfoV1(email, locationId);

        return ApiResponse.onSuccess(weatherInfo);
    }

    @GetMapping("/api/v2/test/main/weather")
    public ApiResponse<WeatherResponseDTO.MainPageWeatherData> getMainPageResourceV2(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) Long locationId) {

        String email = TokenConverter.getEmailByToken(authorization);
        WeatherResponseDTO.MainPageWeatherData weatherInfo = TestMainPageService.getWeatherInfoV2(email, locationId);

        return ApiResponse.onSuccess(weatherInfo);
    }

    @GetMapping("/api/v3/test/main/weather")
    public ApiResponse<WeatherResponseDTO.MainPageWeatherData> getMainPageResourceV3(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) Long locationId) {

        String email = TokenConverter.getEmailByToken(authorization);
        WeatherResponseDTO.MainPageWeatherData weatherInfo = TestMainPageService.getWeatherInfoV3(email, locationId);

        return ApiResponse.onSuccess(weatherInfo);
    }

    @GetMapping("/api/v4/test/main/weather")
    public ApiResponse<WeatherResponseDTO.MainPageWeatherData> getMainPageResourceV4(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) Long locationId) {

        String email = TokenConverter.getEmailByToken(authorization);
        WeatherResponseDTO.MainPageWeatherData weatherInfo = TestMainPageService.getWeatherInfoV4(email, locationId);

        return ApiResponse.onSuccess(weatherInfo);
    }

    @GetMapping("/test/api/v1/main/posts/popular")
    public ApiResponse<List<PostResponse.Post>> getLatestPostList(
            @RequestHeader("Authorization") String authorization) {

        String email = TokenConverter.getEmailByToken(authorization);
        List<PostResponse.Post> latestPostList = mainPageService.getLatestPostList(email);
        return ApiResponse.onSuccess(latestPostList);
    }

    @GetMapping(value = "/test/api/v1/main/extraWeatherInfo")
    public ApiResponse<WeatherResponseDTO.ExtraWeatherInfo> getExtraWeatherInfo(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) Long locationId) {

        String email = getEmailByToken(authorization);
        WeatherResponseDTO.ExtraWeatherInfo extraWeatherInfo = weatherService.extraWeatherInfo(email, locationId);

        return ApiResponse.onSuccess(extraWeatherInfo);
    }

    @GetMapping("/test/api/v1/main/weather/simple/tags")
    public ApiResponse<List<TagDto.SimpleTag>> getMostSelectedTags(
            @RequestHeader("Authorization") String authorization) {
        String email = TokenConverter.getEmailByToken(authorization);
        List<TagDto.SimpleTag> mostTags = mainPageService.getMostSelectedTags(email);
        return ApiResponse.onSuccess(mostTags);
    }
}
