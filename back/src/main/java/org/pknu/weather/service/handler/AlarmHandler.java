package org.pknu.weather.service.handler;

import org.pknu.weather.service.supports.AlarmType;

public interface AlarmHandler {
    AlarmType getAlarmType();
    void handleRequest();
}
