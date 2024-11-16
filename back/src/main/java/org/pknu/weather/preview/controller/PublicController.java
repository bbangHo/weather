package org.pknu.weather.preview.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.preview.dto.Request.WeatherSurvey;
import org.pknu.weather.preview.service.PreviewService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {
    private final PreviewService previewService;

    @PostMapping("/survey")
    public ApiResponse<Object> createSurvey(WeatherSurvey survey) {

        return null;
    }

    @GetMapping("/chart")
    public String showChart(Model model) {
        List<Integer> barData = Arrays.asList(12, 19, 3, 5, 2, 3);
        List<Integer> lineData = Arrays.asList(8, 15, 5, 10, 4, 6);
        List<String> labels = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun");

        model.addAttribute("barData", barData);
        model.addAttribute("lineData", lineData);
        model.addAttribute("labels", labels);

        return "chart";
    }

}
