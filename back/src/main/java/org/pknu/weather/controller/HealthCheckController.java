package org.pknu.weather.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health-check")
    public Map<String, Integer> healthCheck() {
        Map<String, Integer> map = new HashMap<>();
        map.put("code", 200);
        return map;
    }

}
