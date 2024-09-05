package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.dto.LocationDTO;
import org.pknu.weather.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/location")
public class LocationControllerV1 {
    private final LocationService locationService;

    @PostMapping("/coor")
    public ApiResponse<LocationDTO> saveMemberLocation( @RequestHeader("Authorization") String authorization,
                                                        @RequestBody Map<String, Object> payload) {
        log.debug("/api/v1/location/coor controller start ............");

        String email = getEmailByToken(authorization);

        double longitude = Double.parseDouble(String.valueOf(payload.get("longitude")));
        double latitude = Double.parseDouble(String.valueOf(payload.get("latitude")));

        LocationDTO savedLocation = locationService.saveLocation(email,longitude, latitude);

        return ApiResponse.onSuccess(savedLocation);
    }

    @GetMapping("/defaultLoc")
    public ApiResponse<LocationDTO> getMemberDefaultLocation(@RequestHeader("Authorization") String authorization) {

        log.debug("/api/v1/location/defaultLoc controller start ............");

        String email = getEmailByToken(authorization);

        LocationDTO savedLocation = locationService.findDefaultLocation(email);

        return ApiResponse.onSuccess(savedLocation);
    }
}