package org.pknu.weather.domain.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public enum RainTag implements EnumTag {
    NOTHING("", "안옴", 1),
    ALMOST_NOTHING("거의", "안옴", 2),
    VERY_WEAK("매우", "약함", 3),
    WEAK("", "약함", 4),
    AVERAGE("", "보통", 5),
    STRONG("", "강함", 6),
    LITTLE_STRONG("조금", "강함", 7),
    VERY_STRONG("매우", "강함", 8);

    private final String Adverb;
    private final String text;
    private final Integer code;

    @Override
    public String toString() {
        String string = getAdverb() + " " + getText();
        return string.trim();
    }

    @Override
    public EnumTag findByCode(int code) {
        for(RainTag tag : RainTag.values()) {
            if(tag.getCode().equals(code))
                return tag;
        }

        return RainTag.NOTHING;
    }
}
