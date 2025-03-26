package org.pknu.weather.domain.tag;

public interface EnumTag {
    EnumTag findByCode(int code);
    String getKey();
    String getText();
    Integer getCode();

    default String toText() {
        return (getAdverb() + " " + getText()).trim();
    }

    default String getAdverb() {
        return "";
    };

    default String getTagName() {
        String[] split = getClass().toString().split("\\.");
        return split[split.length - 1];
    }
}
