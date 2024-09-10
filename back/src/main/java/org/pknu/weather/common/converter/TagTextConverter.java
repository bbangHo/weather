package org.pknu.weather.common.converter;

import org.pknu.weather.domain.tag.HumidityTag;
import org.pknu.weather.domain.tag.TemperatureTag;

public class TagTextConverter {

    public static String temperatureHumidityTag2Text(TemperatureTag temperatureTag, HumidityTag humidityTag) {
        String humidity = null;
        String temperature = null;

        switch (humidityTag.getCode()) {
            case 1: humidity = "건조하고 "; break;
            case 3: humidity = "약간 습하고 "; break;
            case 4: humidity = "습하면서 "; break;
            case 5: humidity = "매우 습하고 "; break;
            default: humidity = "안 습하고 "; break;
        }

        switch (temperatureTag.getCode()) {
            case 1: temperature = "매우 추워요"; break;
            case 2: temperature = "추워요"; break;
            case 3: temperature = "조금 추워요"; break;
            case 4: temperature = "선선해요"; break;
            case 6: temperature = "따뜻해요"; break;
            case 7: temperature = "조금 따뜻해요"; break;
            case 8: temperature = "조금 더워요"; break;
            case 9: temperature = "더워요"; break;
            case 10: temperature = "매우 더워요"; break;
            default: temperature = "무난해요"; break;
        }

        return humidity + temperature;
    }
}
