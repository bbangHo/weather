package org.pknu.weather.dto.converter;

import org.pknu.weather.dto.TagDto;
import org.pknu.weather.dto.TagQueryResult;

import java.util.List;

public class TagResponseConverter {

    public static TagDto.SimpleTag toSimpleTag(String text) {
        return TagDto.SimpleTag.builder()
                .text(text)
                .build();
    }

}
