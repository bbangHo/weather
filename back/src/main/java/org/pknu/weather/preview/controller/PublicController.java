package org.pknu.weather.preview.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.preview.dto.Request.WeatherSurvey;
import org.pknu.weather.preview.service.PreviewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {
    private final PreviewService previewService;

    @PostMapping("/survey")
    public ApiResponse<Object> createSurvey(WeatherSurvey survey) {

        return null;
    }
}
