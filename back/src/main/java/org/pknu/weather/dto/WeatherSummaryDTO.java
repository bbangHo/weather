package org.pknu.weather.dto;

import lombok.Data;

@Data
public class WeatherSummaryDTO {

    private Long locationId;

    private String rainStatus;

    private Integer maxTemp;

    private Integer minTemp;

}
