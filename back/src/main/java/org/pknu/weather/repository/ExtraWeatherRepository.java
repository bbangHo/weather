package org.pknu.weather.repository;

import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExtraWeatherRepository extends JpaRepository<ExtraWeather, Long> {
}
