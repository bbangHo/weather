package org.pknu.weather.common.utils;

import java.util.List;
import java.util.Optional;
import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.common.RainType;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.tag.DustTag;
import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.domain.tag.HumidityTag;
import org.pknu.weather.domain.tag.RainTag;
import org.pknu.weather.domain.tag.SkyTag;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.domain.tag.WindTag;

public class TagUtils {

    private static final int VERY_HOT = 32;
    private static final int HOT = 27;
    private static final int LITTLE_HOT = 23;
    private static final int AVERAGE = 20;
    private static final int COOL = 17;
    private static final int LITTLE_COLD = 13;
    private static final int COLD = 8;

    public static Boolean tagSelectedOrNot(EnumTag tag, Weather weather, Optional<ExtraWeather> extraWeatherOptional) {
        if (tag instanceof WindTag) {
            return tag == windSpeed2WindTag(weather.getWindSpeed());
        } else if (tag instanceof HumidityTag) {
            return tag == humidity2HumidityTag(weather.getHumidity());
        } else if (tag instanceof SkyTag) {
            return tag == SkyTag.CLEAR;
        } else if (tag instanceof DustTag) {
            return extraWeatherOptional.map(extraWeather -> tag == pmValues2DustTag(extraWeather))
                    .orElseGet(() -> tag == DustTag.GOOD);
        }
        return false;
    }

    public static RainTag rainType2RainTag(Weather weather) {
        Float rain = weather.getRain();
        Float snowCover = weather.getSnowCover();
        RainType rainType = weather.getRainType();

        switch (rainType) {
            case RAIN -> {
                return rain2RainTag(rain);
            }
            case RAIN_AND_SNOW -> {
                return rainAndSnow2RainTag(rain);
            }
            case SNOW -> {
                return snow2RainTag(snowCover);
            }
            case SHOWER -> {
                return RainTag.SHOWER;
            }
            default -> {
                return RainTag.NOTHING;
            }
        }
    }

    private static RainTag snow2RainTag(Float snowCover) {
        if (snowCover == 0.0) {
            return RainTag.NOTHING;
        } else if (snowCover <= 1.0) {
            return RainTag.LIGHT_SNOW;
        } else if (snowCover <= 3.0) {
            return RainTag.MODERATE_SNOW;
        } else {
            return RainTag.HEAVY_SNOW;
        }
    }

    private static RainTag rainAndSnow2RainTag(Float rain) {
        if (rain == 0.0) {
            return RainTag.NOTHING;
        } else if (rain <= 3.0) {
            return RainTag.LIGHT_RAIN_AND_SNOW;
        } else if (rain <= 15.0) {
            return RainTag.MODERATE_RAIN_AND_SNOW;
        } else {
            return RainTag.EXTREME_RAIN_AND_SNOW;
        }
    }

    private static RainTag rain2RainTag(Float rain) {
        if (rain == 0.0) {
            return RainTag.NOTHING;
        } else if (rain <= 3.0) {
            return RainTag.LIGHT_RAIN;
        } else if (rain <= 15.0) {
            return RainTag.MODERATE_RAIN;
        } else {
            return RainTag.EXTREME_RAIN;
        }
    }

    public static DustTag pmValues2DustTag(ExtraWeather extraWeather) {
        Integer pm10Value = extraWeather.getPm10value();
        Integer pm25value = extraWeather.getPm25value();

        DustTag pm10;
        DustTag pm25;

        if (pm10Value <= 30) {
            pm10 = DustTag.GOOD;
        } else if (pm10Value <= 50) {
            pm10 = DustTag.NORMAL;
        } else if (pm10Value <= 150) {
            pm10 = DustTag.LITTLE_BAD;
        } else {
            pm10 = DustTag.VERY_BAD;
        }

        if (pm25value <= 30) {
            pm25 = DustTag.GOOD;
        } else if (pm25value <= 50) {
            pm25 = DustTag.NORMAL;
        } else if (pm25value <= 150) {
            pm25 = DustTag.LITTLE_BAD;
        } else {
            pm25 = DustTag.VERY_BAD;
        }
        if (pm10.getCode() > pm25.getCode()) {
            return pm10;
        } else {
            return pm25;
        }
    }

    public static WindTag windSpeed2WindTag(Double windSpeed) {
        if (windSpeed < 1.5) {
            return WindTag.NONE;
        } else if (windSpeed < 8.0) {
            return WindTag.NORMAL_WINDY;
        } else if (windSpeed < 10.8) {
            return WindTag.WEAK_WINDY;
        } else {
            return WindTag.VERY_WINDY;
        }
    }

    public static HumidityTag humidity2HumidityTag(Integer humidity) {
        if (humidity < 30) {
            return HumidityTag.DRY;
        } else if (humidity < 37) {
            return HumidityTag.COMMON_HUMID;
        } else if (humidity < 44) {
            return HumidityTag.LITTLE_HUMID;
        } else if (humidity < 52) {
            return HumidityTag.HUMID;
        } else {
            return HumidityTag.VERY_HUMID;
        }
    }

    public static TemperatureTag tmp2TemperatureTag(Integer tmp, Sensitivity sensitivity) {
        int adjustment = 0;
        int weight = 1;

        if (sensitivity == Sensitivity.HOT) {
            adjustment = -3;
        }

        if (sensitivity == Sensitivity.COLD) {
            adjustment = 3;
            weight = -1;
        }

        if (sensitivity == Sensitivity.NONE) {
            weight = 0;
        }

        if (tmp >= VERY_HOT + adjustment) {
            return TemperatureTag.VERY_HOT;
        } else if (tmp >= HOT + adjustment + weight) {
            return TemperatureTag.HOT;
        } else if (tmp >= LITTLE_HOT + adjustment + 2 * weight) {
            return TemperatureTag.LITTLE_HOT;
        } else if (tmp >= AVERAGE) {
            return TemperatureTag.COMMON;
        } else if (tmp >= COOL) {
            return TemperatureTag.COOL;
        } else if (tmp >= LITTLE_COLD + adjustment + 2 * weight) {
            return TemperatureTag.LITTLE_COLD;
        } else if (tmp >= COLD + adjustment + weight) {
            return TemperatureTag.COLD;
        } else {
            return TemperatureTag.VERY_COLD;
        }
    }

    public static String temperatureAndHumidityTag2TemperatureTag(List<EnumTag> list) {
        HumidityTag humidityTag = null;
        TemperatureTag temperatureTag = null;

        for (EnumTag tag : list) {
            if (isTemperatureTag(tag)) {
                temperatureTag = (TemperatureTag) tag;
            }

            if (isHumidityTag(tag)) {
                humidityTag = (HumidityTag) tag;
            }
        }

        assert temperatureTag != null;
        assert humidityTag != null;

        String temperature = tag2Text(temperatureTag);
        String humidity = tag2Text(humidityTag);

        return humidity + ", " + temperature;
    }

    public static String tag2Text(EnumTag tag) {
        if (isTemperatureTag(tag)) {
            return temperatureTag2Text(tag);
        } else if (isWindTag(tag)) {
            return windTag2Text(tag);
        } else if (isHumidityTag(tag)) {
            return humidityTag2Text(tag);
        } else if (isSkyTag(tag)) {
            return skyTag2Text(tag);
        } else {
            return dustTag2Text(tag);
        }
    }

    private static String skyTag2Text(EnumTag skyTag) {
        assert skyTag instanceof SkyTag;
        return skyTag.toText();
    }

    private static String dustTag2Text(EnumTag dustTag) {
        assert dustTag instanceof DustTag;
        return "미세먼지 " + dustTag.toText();
    }

    private static String humidityTag2Text(EnumTag humidityTag) {
        assert humidityTag instanceof HumidityTag;
        return humidityTag.toText();
    }

    private static String temperatureTag2Text(EnumTag temperatureTag) {
        assert temperatureTag instanceof TemperatureTag;
        return temperatureTag.toText();
    }

    private static String windTag2Text(EnumTag windTag) {
        assert windTag instanceof WindTag;
        String wind = "바람 ";
        Integer code = windTag.getCode();
        return switch (code) {
            case 1 -> wind + windTag.findByCode(code).getText();
            case 3 -> wind + windTag.findByCode(code).getText();
            default -> wind + windTag.findByCode(code).getText();
        };
    }

    public static boolean isTempTagOrHumdiTag(EnumTag tag) {
        return isTemperatureTag(tag) || isHumidityTag(tag);
    }

    public static boolean isTemperatureTag(EnumTag tag) {
        return tag.getClass() == TemperatureTag.class;
    }

    public static boolean isHumidityTag(EnumTag tag) {
        return tag.getClass() == HumidityTag.class;
    }

    public static boolean isSkyTag(EnumTag tag) {
        return tag.getClass() == SkyTag.class;
    }

    public static boolean isDustTag(EnumTag tag) {
        return tag.getClass() == DustTag.class;
    }

    public static boolean isWindTag(EnumTag tag) {
        return tag.getClass() == WindTag.class;
    }
}
