package org.pknu.weather.domain.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkyTag implements EnumTag {
    RAIN("비와요",  1),
    CLOUDY("흐려요",  2),
    CLEAR_AND_CLOUDY("맑고 구름이 많아요",  3),
    CLEAR("맑아요",  4),
    SUNNY("화창해요",  5)
    ;

    private final String text;
    private final Integer code;

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public EnumTag findByCode(int code) {
        for(SkyTag tag : SkyTag.values()) {
            if(tag.getCode().equals(code))
                return tag;
        }

        return SkyTag.CLEAR;
    }
}
