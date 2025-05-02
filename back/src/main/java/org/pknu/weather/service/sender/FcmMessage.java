package org.pknu.weather.service.sender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FcmMessage implements NotificationMessage {
    private String fcmToken;
    private String alarmTitle;
    private String alarmMessage;
}
