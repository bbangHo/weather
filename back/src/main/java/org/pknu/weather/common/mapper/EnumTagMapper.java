package org.pknu.weather.common.mapper;

import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.dto.TagDto;

import java.util.*;
import java.util.stream.Collectors;

public class EnumTagMapper {
    private final Map<String, EnumTag> map = new HashMap<>();

    private List<EnumTag> toEnumTagValues(Class<? extends EnumTag> e) {
        return Arrays
                .stream(e.getEnumConstants())
                .collect(Collectors.toList());
    }

    public void put(Class<? extends EnumTag> e) {
        List<EnumTag> enumTagValues = toEnumTagValues(e);

        for (EnumTag enumTagValue : enumTagValues) {
            map.put(enumTagValue.getKey(), enumTagValue);
        }
    }

    public EnumTag get(String key) {
        return map.get(key);
    }

    public Map<String, List<TagDto>> getAll() {
        Map<String, List<TagDto>> list = new HashMap<>();

        map.values().forEach(tag -> {
            TagDto tagDto = new TagDto(tag);
            String tagName = tag.getTagName();

            if (!list.containsKey(tagName)) {
                list.put(tagName, new LinkedList<>());
            }

            list.get(tagName).add(tagDto);
        });

        list.forEach((s, dtoList) -> {
            dtoList.sort((o1, o2) -> o1.getCode() - o2.getCode());
        });

        return list;
    }
}
