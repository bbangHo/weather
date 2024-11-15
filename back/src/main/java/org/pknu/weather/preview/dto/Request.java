package org.pknu.weather.preview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class Request {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherSurvey {
        private String gender;
        private String weatherSensitivity;
        private String todayFeelingTemperature;
        private String skyCondition;
        private String humidity;
        private String windy;
        private String comment;
    }
}
