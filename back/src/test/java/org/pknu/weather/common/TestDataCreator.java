package org.pknu.weather.common;

import java.time.LocalDateTime;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;

public class TestDataCreator {
    private static int locationIdx = 1;
    private static int memberIdx = 1;

    public static Member getMember() {
        return Member.builder()
                .location(getBusanLocation())
                .nickname("busan member")
                .build();
    }

    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.now()
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    public static LocalDateTime getLocalDateTimePlusHours(int hours) {
        return getLocalDateTime().plusHours(hours);
    }

    public static Location getBusanLocation() {
        return Location.builder()
                .point(GeometryUtils.getPoint(TestGlobalParams.BusanGeometry.LATITUDE,
                        TestGlobalParams.BusanGeometry.LONGITUDE))
                .city("시군구")
                .province("부산광역시" + locationIdx++)
                .street("읍면동")
                .latitude(TestGlobalParams.BusanGeometry.LATITUDE)
                .longitude(TestGlobalParams.BusanGeometry.LONGITUDE)
                .build();
    }

    public static Location getSeoulLocation() {
        return Location.builder()
                .point(GeometryUtils.getPoint(TestGlobalParams.SeoulGeometry.LATITUDE,
                        TestGlobalParams.SeoulGeometry.LONGITUDE))
                .city("시군구")
                .province("서울광역시" + locationIdx++)
                .street("읍면동")
                .latitude(TestGlobalParams.SeoulGeometry.LATITUDE)
                .longitude(TestGlobalParams.SeoulGeometry.LONGITUDE)
                .build();
    }

}
