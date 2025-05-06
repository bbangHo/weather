package org.pknu.weather.event;

import lombok.Getter;

@Getter
public class RecommendEvent extends AbstractExpEvent {
    public RecommendEvent(String targetEmail) {
        super(targetEmail);
    }
}
