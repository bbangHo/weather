package org.pknu.weather.preview.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.domain.tag.TemperatureTag;

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
        private Integer count;
        private String time;
    }
}
