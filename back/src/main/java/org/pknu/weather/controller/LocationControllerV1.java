package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.dto.LocationDTO;
import org.pknu.weather.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/location")
public class LocationControllerV1 {
    private final LocationService locationService;

    @PostMapping("/coor")
    public ApiResponse<LocationDTO> saveMemberLocation( @RequestBody Map<String, Object> payload) {

        double longitude = Double.parseDouble(String.valueOf(payload.get("longitude")));
        double latitude = Double.parseDouble(String.valueOf(payload.get("latitude")));

        LocationDTO savedLocation = locationService.saveLocation(longitude, latitude);

        return ApiResponse.onSuccess(savedLocation);
    }
}
