package org.pknu.weather.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherQueryResult;

public interface WeatherCustomRepository {
    boolean weatherHasBeenUpdated(Location location);

    boolean weatherHasBeenCreated(Location location);

    WeatherQueryResult.SimpleRainInfo getSimpleRainInfo(Location locationEntity);

    List<Weather> getTemperatureForHour(LocalDateTime startTime);
}
