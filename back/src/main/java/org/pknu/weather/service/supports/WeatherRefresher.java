package org.pknu.weather.service.supports;

import static org.pknu.weather.dto.converter.ExtraWeatherConverter.toExtraWeather;
import static org.pknu.weather.dto.converter.LocationConverter.toLocationDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherResponse.ExtraWeatherInfo;
import org.pknu.weather.feignClient.utils.ExtraWeatherApiUtils;
import org.pknu.weather.feignClient.utils.WeatherFeignClientUtils;
import org.pknu.weather.repository.ExtraWeatherRepository;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.service.WeatherQueryService;
import org.pknu.weather.service.WeatherService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class WeatherRefresher {

    private final LocationRepository locationRepository;
    private final ExtraWeatherRepository extraWeatherRepository;
    private final ExtraWeatherApiUtils extraWeatherApiUtils;
    private final WeatherQueryService weatherQueryService;
    private final WeatherService weatherService;

    public void refresh(Set<Long> locationIds) {
        List<Location> locations = locationRepository.findByIdIn(locationIds);
        for (Location location : locations) {
            updateWeather(location);
            updateExtraWeather(location);
        }
    }

    private void updateWeather(Location location) {

        if (!weatherQueryService.weatherHasBeenCreated(location)) {
            weatherService.saveWeathers(location);
        }

        if (!weatherQueryService.weatherHasBeenUpdated(location)) {
            weatherService.updateWeathers(location.getId());
        }
    }

    public void updateExtraWeather(Location location) {
        extraWeatherRepository.findByLocationId(location.getId())
                .ifPresentOrElse(
                        extraWeather -> updateExistingExtraWeather(location, extraWeather),
                        () -> saveExtraWeather(location)
                );
    }

    private void updateExistingExtraWeather(Location location, ExtraWeather extraWeather) {
        if (extraWeather.getBasetime().isBefore(LocalDateTime.now().minusHours(3))) {
            ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(
                    toLocationDTO(location), extraWeather.getBasetime());
            extraWeather.updateExtraWeather(extraWeatherInfo);
            extraWeatherRepository.save(extraWeather);
        }
    }

    private void saveExtraWeather(Location location) {
        ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(
                toLocationDTO(location));

        extraWeatherRepository.save(toExtraWeather(location, extraWeatherInfo));
    }
}
