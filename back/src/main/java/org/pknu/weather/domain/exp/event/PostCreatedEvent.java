package org.pknu.weather.domain.exp.event;

import lombok.Getter;

@Getter
public class PostCreatedEvent extends AbstractExpEvent {
    public PostCreatedEvent(String targetEmail) {
        super(targetEmail);
    }
}
