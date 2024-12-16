package org.pknu.weather.repository;

import org.pknu.weather.domain.TagWeather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagWeatherRepository extends JpaRepository<TagWeather, Long> {
}
