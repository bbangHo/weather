package org.pknu.weather.common.utils;

import org.junit.jupiter.api.Test;
import org.pknu.weather.feignClient.dto.PointDTO;

class GeometryUtilsTest {

    @Test
    void test() {
        PointDTO pointDTO = GeometryUtils.coordinateToPoint(128.73581239800174, 35.89540475841895);

        System.out.println(pointDTO.getX());
        System.out.println(pointDTO.getY());
    }

}