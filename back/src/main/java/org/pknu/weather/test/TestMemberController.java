package org.pknu.weather.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.mainpage.service.MainPageService;
import org.pknu.weather.weather.service.WeatherService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class TestMemberController {
    private final MainPageService mainPageService;
    private final WeatherService weatherService;
}