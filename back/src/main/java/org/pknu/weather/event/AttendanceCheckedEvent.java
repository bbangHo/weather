package org.pknu.weather.event;

import lombok.Getter;

@Getter
public class AttendanceCheckedEvent extends AbstractExpEvent {
    public AttendanceCheckedEvent(String targetEmail) {
        super(targetEmail);
    }
}
