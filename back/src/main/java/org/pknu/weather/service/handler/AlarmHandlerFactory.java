package org.pknu.weather.service.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.common.AlarmType;
import org.pknu.weather.exception.GeneralException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmHandlerFactory {

    private final List<ArgsAlarmHandler<?>> argsAlarmHandlers;
    private final List<NoArgsAlarmHandler> noArgsAlarmHandlers;


    public NoArgsAlarmHandler getNoArgsAlarmHandler(AlarmType type) {
        return noArgsAlarmHandlers.stream()
                .filter(handler -> handler.getAlarmType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 알람 타입: " + type));
    }

    public <T> ArgsAlarmHandler<T> getArgsAlarmHandler(AlarmType type, Class<T> expectedArgsType) {

        ArgsAlarmHandler<?> argsAlarmHandler = argsAlarmHandlers.stream()
                .filter(handler -> handler.getAlarmType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 알람 타입: " + type));

        if (expectedArgsType != type.getArgumentType()) {
            throw new GeneralException(ErrorStatus._FCMTOKEN_NOT_FOUND);
        }

        try {
            @SuppressWarnings("unchecked")
            ArgsAlarmHandler<T> specificHandler = (ArgsAlarmHandler<T>) argsAlarmHandler;

            return specificHandler;
        } catch (ClassCastException e) {
            throw new IllegalStateException("알람 타입과 핸들러가 호환되지 않습니다: " + type, e);
        }
    }
}
