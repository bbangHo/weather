package org.pknu.weather.repository;

import org.pknu.weather.domain.Location;

public interface WeatherCustomRepository {
    boolean weatherHasBeenUpdated(Location location);
    boolean weatherHasBeenCreated(Location location);
}
