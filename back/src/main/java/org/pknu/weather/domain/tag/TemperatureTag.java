package org.pknu.weather.domain.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TemperatureTag implements EnumTag {
    VERY_COLD("매우", "추움", 1),
    COLD("", "추움", 2),
    LITTLE_COLD("조금", "추움", 3),
    COOL("", "선선", 4),
    NORMAL("", "보통", 5),
    WARM("", "따뜻", 6),
    LITTLE_WARM("조금", "따뜻", 7),
    LITTLE_HOT("조금", "더움", 8),
    HOT("", "더움", 9),
    VERY_HOT("매우", "더움", 10);

    private final String adverb;
    private final String text;
    private final Integer code;

    @Override
    public String toString() {
        String string = getAdverb() + " " + getText();
        return string.trim();
    }

    @Override
    public EnumTag findByCode(int code) {
        for(TemperatureTag tag : TemperatureTag.values()) {
            if(tag.getCode().equals(code))
                return tag;
        }

        return TemperatureTag.NORMAL;
    }
}
