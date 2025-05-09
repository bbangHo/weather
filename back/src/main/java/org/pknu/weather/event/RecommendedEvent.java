package org.pknu.weather.event;

import lombok.Getter;

@Getter
public class RecommendedEvent extends AbstractExpEvent {
    public RecommendedEvent(String targetEmail) {
        super(targetEmail);
    }
}
