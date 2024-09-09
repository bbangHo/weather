package org.pknu.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.tag.*;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class TagQueryResult {
    private TemperatureTag tempCount;
    private WindTag windCount;
    private HumidityTag humidityCount;
    private SkyTag skyCount;
    private DustTag dustCount;

//    @QueryProjection
//    public TagQueryResult(Long tempCount, Long windCount, Long humidityCount, Long skyCount, Long dustCount) {
//        this.tempCount = tempCount;
//        this.windCount = windCount;
//        this.humidityCount = humidityCount;
//        this.skyCount = skyCount;
//        this.dustCount = dustCount;
//    }
}
