package org.pknu.weather.dto;

import lombok.*;
import org.pknu.weather.domain.tag.*;

@Getter
@Builder
@AllArgsConstructor
public class TagQueryResult {
    private final EnumTag tag;
    private final Long count;
}
