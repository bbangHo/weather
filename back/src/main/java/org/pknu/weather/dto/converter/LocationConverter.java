package org.pknu.weather.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.dto.LocationDTO;
import org.pknu.weather.dto.PostRequest;

@Slf4j
public class LocationConverter {

    public static Location toLocation(LocationDTO locationDTO) {
        return Location.builder()
                .longitude(locationDTO.getLongitude())
                .latitude(locationDTO.getLatitude())
                .province(locationDTO.getProvince())
                .city(locationDTO.getCity())
                .street(locationDTO.getStreet())
                .build();
    }

    public static LocationDTO toLocationDTO(Location location) {
        return LocationDTO.builder()
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .province(location.getProvince())
                .city(location.getCity())
                .street(location.getStreet())
                .build();
    }
}
