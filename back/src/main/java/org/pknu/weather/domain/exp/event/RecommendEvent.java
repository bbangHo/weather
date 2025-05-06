package org.pknu.weather.domain.exp.event;

import lombok.Getter;

@Getter
public class RecommendEvent extends AbstractExpEvent {
    public RecommendEvent(String targetEmail) {
        super(targetEmail);
    }
}
