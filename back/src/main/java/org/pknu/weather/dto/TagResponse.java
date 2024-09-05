package org.pknu.weather.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.pknu.weather.domain.tag.*;

public class TagResponse {

    public static class MostSelectedTag {
        private String top1;
        private String top2;
        private String top3;
    }

    public static class Tag {
        private String temperTag;
        private String windTag;
        private String humidityTag;
        private String skyTag;
        private String dustTag;
    }
}
