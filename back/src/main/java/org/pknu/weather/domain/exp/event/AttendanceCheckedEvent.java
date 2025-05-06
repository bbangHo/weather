package org.pknu.weather.domain.exp.event;

import lombok.Getter;

@Getter
public class AttendanceCheckedEvent extends AbstractExpEvent {
    public AttendanceCheckedEvent(String targetEmail) {
        super(targetEmail);
    }
}
