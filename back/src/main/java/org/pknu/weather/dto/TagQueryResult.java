package org.pknu.weather.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TagQueryResult {
    private Long tempCount;
    private Long windCount;
    private Long humidityCount;
    private Long skyCount;
    private Long dustCount;

    @QueryProjection
    public TagQueryResult(Long tempCount, Long windCount, Long humidityCount, Long skyCount, Long dustCount) {
        this.tempCount = tempCount;
        this.windCount = windCount;
        this.humidityCount = humidityCount;
        this.skyCount = skyCount;
        this.dustCount = dustCount;
    }
}
