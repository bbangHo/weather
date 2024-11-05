package org.pknu.weather.common;

import org.junit.jupiter.api.Test;
import org.pknu.weather.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
public class Dummy {
    @Autowired
    LocationRepository locationRepository;

    @Test
    @Rollback(value = false)
    void test() {
        locationRepository.save(TestDataCreator.getBusanLocation());
    }
}
