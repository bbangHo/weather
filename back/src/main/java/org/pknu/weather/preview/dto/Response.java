package org.pknu.weather.preview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.domain.common.Sensitivity;

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
        private Type coldSensitivity;
        private Type normalSensitivity ;
        private Type hotSensitivity;
        private Type totalSensitivity;
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
    public static class Type {
        private Sensitivity sensitivity;
        private Integer veryCold;
        private Integer cold;
        private Integer littleCold;
        private Integer cool;
        private Integer common;
        private Integer warm;
        private Integer littleWarm;
        private Integer littleHot;
        private Integer hot;
        private Integer veryHot;
    }
}
