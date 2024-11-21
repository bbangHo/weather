package org.pknu.weather.preview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.domain.tag.TemperatureTag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Response {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dashboard {
        private Integer post;
        private List<TimeAndTemp> timeAndTemps = new ArrayList<>();
        private TagHour coldSensitivity;
        private TagHour normalSensitivity;
        private TagHour hotSensitivity;
        private TagHour totalSensitivity;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeAndTemp {
        private LocalDateTime time;
        private Integer temp;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagHour {
        private TemperatureTag temperatureTag;
        private String time;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private Double windChillTemp;   // 체감온도
        private String tag;
    }
}
