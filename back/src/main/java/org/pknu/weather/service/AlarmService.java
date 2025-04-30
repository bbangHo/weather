package org.pknu.weather.service;


import lombok.RequiredArgsConstructor;
import org.pknu.weather.service.supports.AlarmHandler;
import org.pknu.weather.service.supports.AlarmHandlerFactory;
import org.pknu.weather.service.supports.AlarmType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmHandlerFactory handlerFactory;

    public void trigger(AlarmType alarmType) {
        AlarmHandler handler = handlerFactory.getHandler(alarmType);
        handler.handleRequest();
    }
}
