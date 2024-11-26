package org.pknu.weather.repository;

import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherQueryResult;

public interface WeatherCustomRepository {
    boolean weatherHasBeenUpdated(Location location);
    boolean weatherHasBeenCreated(Location location);
    WeatherQueryResult.SimpleRainInfo getSimpleRainInfo(Location locationEntity);
    Weather findByLocationClosePresentationTime(Location location);
}
