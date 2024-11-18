package org.pknu.weather.preview.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.preview.dto.Request.WeatherSurvey;
import org.pknu.weather.preview.dto.Response.TagHour;
import org.pknu.weather.preview.dto.Response.TimeAndTemp;
import org.pknu.weather.preview.service.PreviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final PreviewService previewService;

    @GetMapping
    public String getPage(Model model) {
        List<Member> mostMembers = previewService.getMostMembers();

        Map<Sensitivity, Long> collect = mostMembers.stream()
                .collect(Collectors.groupingBy(Member::getSensitivity, Collectors.counting()));

        Optional<Map.Entry<Sensitivity, Long>> sensitivityLongEntry = collect.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        Long mostSelectTypeCount = sensitivityLongEntry.map(Map.Entry::getValue).orElse(null);
        Sensitivity mostSelectType = sensitivityLongEntry.map(Map.Entry::getKey).orElse(null);

        Integer sharedWeatherCount = previewService.getSharedWeatherCount();
        Integer sharedWeatherIncrease = previewService.getSharedWeatherIncrease();

        Map<TemperatureTag, Long> mostSelectedTag = previewService.getMostSelectedTag();

        TemperatureTag mostSelectTempTag = mostSelectedTag.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Long countUser = mostSelectedTag.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getValue)
                .orElse(null);

        model.addAttribute("mostSelectTempTag", mostSelectTempTag.toText());
        model.addAttribute("countUser", countUser);
        model.addAttribute("sharedWeatherCount", sharedWeatherCount);
        model.addAttribute("sharedWeatherIncrease", "+" + sharedWeatherIncrease);
        model.addAttribute("mostSelectTypeCount", mostSelectTypeCount);
        model.addAttribute("mostSelectType", mostSelectType == Sensitivity.NONE ? "보통" :
                mostSelectType == Sensitivity.HOT ? "더위를 많이 타는" : "추위를 많이 타는");

        return "dashboard";
    }

    @PostMapping("/survey")
    public ApiResponse<Object> createSurvey(WeatherSurvey survey) {

        return null;
    }

    @GetMapping("/row-data")
    public String showRowData(Model model, Integer sensitivity) {

        return "chart";
    }

    @GetMapping("/chart-data")
    @ResponseBody
    public Map<String, Object> showChartData(@RequestParam(defaultValue = "0") Integer sensitivity) {
        List<TimeAndTemp> tempAndTempData = previewService.getTimeAndTemp();
        List<TagHour> tagHours = previewService.getTags(sensitivity);

        List<String> times = tempAndTempData.stream()
                .map(data -> data.getTime().format(DateTimeFormatter.ofPattern("HH")))
                .collect(Collectors.toList());

        List<Integer> temps = tempAndTempData.stream()
                .map(TimeAndTemp::getTemp)
                .collect(Collectors.toList());

        // 시간별로 태그 데이터 그룹화 및 카운트
        Map<String, Map<TemperatureTag, Long>> tagCountsByHour = tagHours.stream()
                .collect(Collectors.groupingBy(TagHour::getTime,
                        Collectors.groupingBy(TagHour::getTemperatureTag, Collectors.counting())));

        // 각 시간대별 상위 3개 태그 선택
        Map<String, List<Map.Entry<TemperatureTag, Long>>> topTagsByHour = new HashMap<>();
        for (Map.Entry<String, Map<TemperatureTag, Long>> entry : tagCountsByHour.entrySet()) {
            List<Map.Entry<TemperatureTag, Long>> topTags = entry.getValue().entrySet().stream()
                    .sorted(Map.Entry.<TemperatureTag, Long>comparingByValue().reversed())
                    .limit(3)
                    .collect(Collectors.toList());
            topTagsByHour.put(entry.getKey(), topTags);
        }

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("times", times);
        response.put("temps", temps);

        // 상위 3개 태그의 시간별 데이터 추출
        Map<TemperatureTag, List<Integer>> tagData = new HashMap<>();
        for (TemperatureTag tag : TemperatureTag.values()) {
            List<Integer> counts = times.stream()
                    .map(time -> topTagsByHour.getOrDefault(time, Collections.emptyList()).stream()
                            .filter(entry -> entry.getKey() == tag)
                            .findFirst()
                            .map(entry -> entry.getValue().intValue())
                            .orElse(0))
                    .collect(Collectors.toList());
            tagData.put(tag, counts);
        }

        // 전체 시간대에서 상위 3개 태그 선택
        List<TemperatureTag> overallTopTags = tagData.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().stream().mapToInt(Integer::intValue).sum(),
                        e1.getValue().stream().mapToInt(Integer::intValue).sum()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 상위 3개 태그 데이터 추가
        for (int i = 0; i < overallTopTags.size(); i++) {
            TemperatureTag tag = overallTopTags.get(i);
            response.put("tag" + (i + 1), tag.toText());
            response.put("tagData" + (i + 1), tagData.get(tag));
        }

        return response;
    }

}
