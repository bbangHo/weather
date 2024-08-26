package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.common.SgisLocationUtils;
import org.pknu.weather.common.feignClient.SgisClient;
import org.pknu.weather.domain.Location;
import org.pknu.weather.dto.LocationDTO;
import org.pknu.weather.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.pknu.weather.dto.converter.LocationConverter.toLocation;
import static org.pknu.weather.dto.converter.LocationConverter.toLocationDTO;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {

    private final SgisLocationUtils sgisLocationUtils;
    private final LocationRepository locationRepository;

    public LocationDTO saveLocation(double x, double y) {


        LocationDTO locationDTO = sgisLocationUtils.getAddressInfo(x, y);

        Optional<Location> locationByFullAddress = locationRepository.findLocationByFullAddress(locationDTO.getProvince(), locationDTO.getCity(), locationDTO.getStreet());

        Location savedLocation = locationByFullAddress.orElseGet(() -> locationRepository.save(toLocation(locationDTO)));

        return toLocationDTO(savedLocation);
    }
}
