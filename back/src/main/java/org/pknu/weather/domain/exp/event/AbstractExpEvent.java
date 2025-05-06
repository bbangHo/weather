package org.pknu.weather.domain.exp.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractExpEvent {
    protected String targetEmail;
}
