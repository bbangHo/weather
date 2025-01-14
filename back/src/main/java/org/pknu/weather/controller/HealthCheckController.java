package org.pknu.weather.controller;

import org.pknu.weather.apiPayload.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health-check")
    public ApiResponse<Object> healthCheck() {
        return ApiResponse.onSuccess();
    }

}
