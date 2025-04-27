package org.pknu.weather.dto;

import lombok.Data;

@Data
public class AlarmMemberDTO {

    private Long id;

    private Long locationId;

    private String fcmToken;

    private Boolean agreeTempAlarm;

    private Boolean agreePrecipAlarm;

    private Boolean agreeDustAlarm;

    private Boolean agreeUvAlarm;

    private Boolean agreeLiveRainAlarm;

}
