package org.pknu.weather.common.converter;

import org.locationtech.proj4j.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CoordinateConverter {

    private static final String srcParam = "EPSG:4166";
    private static final String destParam = "EPSG:5179";

    private static final CoordinateTransform CONVERTER_TO_UTMK = createConverter(srcParam, destParam);

    private static CoordinateTransform createConverter(String srcParam, String destParam) {
        CRSFactory CRS_FACTORY = new CRSFactory();
        CoordinateReferenceSystem srcCRS = CRS_FACTORY.createFromName(srcParam);
        CoordinateReferenceSystem destCRS = CRS_FACTORY.createFromName(destParam);
        CoordinateTransformFactory TRANSFORM_FACTORY = new CoordinateTransformFactory();
        return TRANSFORM_FACTORY.createTransform(srcCRS, destCRS);
    }

    public static Map<String, Double> transformWGS84ToUTMK(double x, double y) {

        ProjCoordinate srcCoor = new ProjCoordinate(x, y); // 경도, 위도
        ProjCoordinate destCoor = new ProjCoordinate();

        CONVERTER_TO_UTMK.transform(srcCoor, destCoor);

        Map<String, Double> result = new ConcurrentHashMap<>();
        result.put("X", destCoor.x);
        result.put("Y", destCoor.y);

        return result;
    }

}
