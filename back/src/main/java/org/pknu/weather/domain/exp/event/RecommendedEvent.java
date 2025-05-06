package org.pknu.weather.domain.exp.event;

import lombok.Getter;

@Getter
public class RecommendedEvent extends AbstractExpEvent {
    public RecommendedEvent(String targetEmail) {
        super(targetEmail);
    }
}
