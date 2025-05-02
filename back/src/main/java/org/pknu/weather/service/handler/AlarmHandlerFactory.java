package org.pknu.weather.service.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.service.supports.AlarmType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmHandlerFactory {

    private final List<AlarmHandler> handlers;

    public AlarmHandler getHandler(AlarmType type) {
        return handlers.stream()
                .filter(handler -> handler.getAlarmType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 알람 타입: " + type));
    }
}
