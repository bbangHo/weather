package org.pknu.weather.controller;

import io.jsonwebtoken.Header;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.common.TokenConverter;
import org.pknu.weather.dto.LocationDTO;
import org.pknu.weather.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/location")
public class LocationControllerV1 {
    private final LocationService locationService;
    private final TokenConverter tokenConverter;

    @PostMapping("/coor")
    public ApiResponse<LocationDTO> saveMemberLocation( @RequestHeader("Authorization") String authorization,
                                                        @RequestBody Map<String, Object> payload) {

        String email = tokenConverter.getEmailByToken(authorization);

        double longitude = Double.parseDouble(String.valueOf(payload.get("longitude")));
        double latitude = Double.parseDouble(String.valueOf(payload.get("latitude")));

        LocationDTO savedLocation = locationService.saveLocation(email,longitude, latitude);

        return ApiResponse.onSuccess(savedLocation);
    }

    @GetMapping("/defaultLoc")
    public ApiResponse<LocationDTO> getMemberDefaultLocation(@RequestHeader("Authorization") String authorization) {

        String email = tokenConverter.getEmailByToken(authorization);

        LocationDTO savedLocation = locationService.findDefaultLocation(email);

        return ApiResponse.onSuccess(savedLocation);
    }
}