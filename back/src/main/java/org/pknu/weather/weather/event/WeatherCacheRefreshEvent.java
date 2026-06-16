package org.pknu.weather.weather.event;

import java.util.List;
import lombok.Getter;
import org.pknu.weather.weather.Weather;

@Getter
public class WeatherCacheRefreshEvent implements WeatherEvent {
    private Long locationId;
    private List<Weather> forecast;

    public WeatherCacheRefreshEvent(Long locationId) {
        this.locationId = locationId;
    }

    public WeatherCacheRefreshEvent(Long locationId, List<Weather> forecast) {
        this.locationId = locationId;
        this.forecast = forecast;
    }

    @Override
    public Long getLocationId() {
        return locationId;
    }
}
