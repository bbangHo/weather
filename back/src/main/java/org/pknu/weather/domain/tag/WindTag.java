package org.pknu.weather.domain.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WindTag implements EnumTag {
    NONE("안불어요", 1),
    WINDY("조금 불어요", 2),
    VERY_WINDY("많이 불어요", 3);

    private final String text;
    private final Integer code;

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public EnumTag findByCode(int code) {
        for (WindTag tag : WindTag.values()) {
            if (tag.getCode().equals(code))
                return tag;
        }

        return WindTag.NONE;
    }
}
