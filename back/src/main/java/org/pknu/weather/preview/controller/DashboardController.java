package org.pknu.weather.preview.controller;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.preview.dto.Request.WeatherSurvey;
import org.pknu.weather.preview.dto.Response.TagHour;
import org.pknu.weather.preview.dto.Response.TimeAndTemp;
import org.pknu.weather.preview.service.PreviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final PreviewService previewService;

    @GetMapping
    public String getPage() {
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
    public Map<String, Object> showChartData(@RequestParam(defaultValue = "0") Integer sensitivity) {
        List<TimeAndTemp> tempAndTempData = previewService.getTimeAndTemp();
        List<TagHour> tagHours = previewService.getTags(sensitivity);

        List<String> times = tempAndTempData.stream()
                .map(data -> data.getTime().format(DateTimeFormatter.ofPattern("HH")))
                .collect(Collectors.toList());

        List<Integer> temps = tempAndTempData.stream()
                .map(TimeAndTemp::getTemp)
                .collect(Collectors.toList());

        // 시간별로 태그 데이터 그룹화
        Map<String, List<TagHour>> tagsByHour = tagHours.stream()
                .collect(Collectors.groupingBy(TagHour::getTime));

        // 각 시간대별 상위 3개 태그 선택
        Map<String, List<TagHour>> topTagsByHour = new HashMap<>();
        for (Map.Entry<String, List<TagHour>> entry : tagsByHour.entrySet()) {
            List<TagHour> topTags = entry.getValue().stream()
                    .sorted(Comparator.comparing(TagHour::getCount).reversed())
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
                            .filter(th -> th.getTemperatureTag() == tag)
                            .findFirst()
                            .map(TagHour::getCount)
                            .orElse(0))
                    .collect(Collectors.toList());
            tagData.put(tag, counts);
        }

        // 상위 3개 태그 데이터 추가
        List<TemperatureTag> topTags = tagData.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().stream().mapToInt(Integer::intValue).sum(),
                        e1.getValue().stream().mapToInt(Integer::intValue).sum()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (int i = 0; i < topTags.size(); i++) {
            TemperatureTag tag = topTags.get(i);
            response.put("tag" + (i + 1), tag.toText());
            response.put("tagData" + (i + 1), tagData.get(tag));
        }

        return response;
    }

}
