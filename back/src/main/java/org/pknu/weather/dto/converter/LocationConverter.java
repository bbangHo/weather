package org.pknu.weather.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.dto.LocationDTO;

import static org.pknu.weather.common.utils.GeometryUtils.getPoint;

@Slf4j
public class LocationConverter {

    public static Location toLocation(LocationDTO locationDTO) {
        return Location.builder()
                .longitude(locationDTO.getLongitude())
                .latitude(locationDTO.getLatitude())
                .province(locationDTO.getProvince())
                .city(locationDTO.getCity())
                .street(locationDTO.getStreet())
                .point(getPoint(locationDTO.getLatitude(), locationDTO.getLongitude()))
                .build();
    }

    public static LocationDTO toLocationDTO(Location location) {
        return LocationDTO.builder()
                .id(location.getId())
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .province(location.getProvince())
                .city(location.getCity())
                .street(location.getStreet())
                .build();
    }
}
