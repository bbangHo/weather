package org.pknu.weather.repository;

import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherQueryResult;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherCustomRepository {
    boolean weatherHasBeenUpdated(Location location);

    boolean weatherHasBeenCreated(Location location);

    WeatherQueryResult.SimpleRainInfo getSimpleRainInfo(Location locationEntity);

    List<Weather> getTemperatureForHour(LocalDateTime startTime);

    Weather findByLocationAndCloseTime(Location location);
}
