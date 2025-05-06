package org.pknu.weather.event;

import lombok.Getter;

@Getter
public class PostCreatedEvent extends AbstractExpEvent {
    public PostCreatedEvent(String targetEmail) {
        super(targetEmail);
    }
}
