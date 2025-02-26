package org.pknu.weather.dto.converter;

import org.pknu.weather.domain.tag.*;
import org.pknu.weather.dto.PostRequest;

import java.util.Arrays;

public class PostRequestConverter {
    public static PostRequest.CreatePost toCreatePost(PostRequest.Params params) {
        return PostRequest.CreatePost.builder()
                .content(params.getContent())
                .temperatureTag((TemperatureTag) toTagFromCode(params.getTemperatureTagCode(), TemperatureTag.class))
                .skyTag((SkyTag) toTagFromCode(params.getSkyTagCode(), SkyTag.class))
                .windTag((WindTag) toTagFromCode(params.getWindTagCode(), WindTag.class))
                .humidityTag((HumidityTag) toTagFromCode(params.getHumidityTagCode(), HumidityTag.class))
                .dustTag((DustTag) toTagFromCode(params.getDustTagCode(), DustTag.class))
                .build();
    }

    public static EnumTag toTagFromCode(int code, Class<? extends EnumTag> enumTag) {
        EnumTag tag = Arrays.stream(enumTag.getEnumConstants()).toList().get(0);
        return tag.findByCode(code);
    }
}
