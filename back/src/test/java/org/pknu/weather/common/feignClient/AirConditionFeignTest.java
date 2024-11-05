/*
package org.pknu.weather.common.feignClient;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.locationtech.proj4j.*;
import org.pknu.weather.feignClient.AirConditionClient;
import org.pknu.weather.feignClient.dto.AirConditionResponseDTO;
import org.pknu.weather.feignClient.dto.AirObservatoryResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.pknu.weather.common.formatter.DateTimeFormatter.getFormattedDate;

@SpringBootTest
@Slf4j
public class AirConditionFeignTest {
    @Autowired
    AirConditionClient airConditionClient;

    @Test
    public void transformWGS84ToUTMK(){

        CRSFactory crsFactory = new CRSFactory();

        String wgs84 = "+proj=longlat +datum=WGS84 +no_defs";
        CoordinateReferenceSystem srcCRS = crsFactory.createFromParameters("WGS84", wgs84);

        String utmkName = "UTMK";
        String utmkProj = "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs";
        CoordinateReferenceSystem destCRS = crsFactory.createFromParameters(utmkName, utmkProj);

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform transform = ctFactory.createTransform(srcCRS, destCRS);

        ProjCoordinate srcCoord = new ProjCoordinate(127.02015, 37.6349 ); // 경도, 위도
        ProjCoordinate destCoord = new ProjCoordinate();

        transform.transform(srcCoord, destCoord);

        System.out.println("Korea TM 좌표계: X = " + destCoord.x + ", Y = " + destCoord.y);

    }

    @Test
    public void transformWGS84ToUTMK2(){

        // CRSFactory로 좌표계 정의
        CRSFactory crsFactory = new CRSFactory();


        CoordinateReferenceSystem srcCRS = crsFactory.createFromName("EPSG:4166");
        CoordinateReferenceSystem destCRS = crsFactory.createFromName("EPSG:5179");

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform transform = ctFactory.createTransform(srcCRS, destCRS);

        ProjCoordinate srcCoord = new ProjCoordinate(129.16792, 35.16665 ); // 경도, 위도
        ProjCoordinate destCoord = new ProjCoordinate();

        transform.transform(srcCoord, destCoord);

        System.out.println("Korea TM 좌표계: X = " + destCoord.x + ", Y = " + destCoord.y);
    }

    @Test
    public void getObservatoryInfo(){
        AirObservatoryResponseDTO result = airConditionClient.getObservatoryInfo("serviceKey", "json", 1151898.5189909204,1686982.765464108, 1.2);

    }

    @Test
    public void getAirInfo(){
        AirConditionResponseDTO result = airConditionClient.getAirConditionInfo("serviceKey", "json", "대연동","DAILY", 1.5);

    }

}
*/
