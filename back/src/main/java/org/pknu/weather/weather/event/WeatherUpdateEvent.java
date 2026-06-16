package org.pknu.weather.weather.event;

import java.util.List;
import lombok.Getter;
import org.pknu.weather.weather.Weather;

@Getter
public class WeatherUpdateEvent implements WeatherEvent {
    private Long locationId;
    private List<Weather> newForecast;

    public WeatherUpdateEvent(Long locationId) {
        this.locationId = locationId;
    }

    public WeatherUpdateEvent(Long locationId, List<Weather> newForecast) {
        this.locationId = locationId;
        this.newForecast = newForecast;
    }

    @Override
    public Long getLocationId() {
        return locationId;
    }
}
