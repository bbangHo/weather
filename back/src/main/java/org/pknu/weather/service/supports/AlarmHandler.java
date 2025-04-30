package org.pknu.weather.service.supports;

public interface AlarmHandler {
    AlarmType getAlarmType();
    void handleRequest();
}
