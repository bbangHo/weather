package org.pknu.weather.service.supports;

import org.junit.jupiter.api.Test; // JUnit 5 사용
import java.util.ArrayList; // 테스트 리스트 생성을 위해 사용
import java.util.List;
import org.pknu.weather.service.handler.AlarmHandler;
import org.pknu.weather.service.handler.AlarmHandlerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlarmHandlerFactoryTest {

    @Test
    void 올바른_핸들러_리턴_테스트() {
        // given
        AlarmHandler weatherUpdateHandler = new DummyAlarmHandler(AlarmType.WEATHER_UPDATE);
        AlarmHandler weatherSummaryAHandler = new DummyAlarmHandler(AlarmType.WEATHER_SUMMARY);

        List<AlarmHandler> handlerList = List.of(weatherSummaryAHandler, weatherUpdateHandler);

        AlarmHandlerFactory factory = new AlarmHandlerFactory(handlerList);

        // when
        AlarmHandler foundHandler = factory.getHandler(AlarmType.WEATHER_SUMMARY);

        //then
        assertThat(foundHandler)
                .isNotNull()
                .extracting(AlarmHandler::getAlarmType)
                .isEqualTo(AlarmType.WEATHER_SUMMARY);
    }


    @Test
    void 잘못된_핸들러_호출_테스트() {
        // Given
        AlarmHandler weatherUpdateHandler = new DummyAlarmHandler(AlarmType.WEATHER_UPDATE);
        List<AlarmHandler> handlerList = List.of(weatherUpdateHandler);

        AlarmHandlerFactory factory = new AlarmHandlerFactory(handlerList);

        // When & Then
        assertThatThrownBy(() -> factory.getHandler(AlarmType.WEATHER_SUMMARY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 알람 타입: " + AlarmType.WEATHER_SUMMARY);

    }


    @Test
    void 빈_핸들러리스트에_핸들러_호출_테스트() {
        List<AlarmHandler> emptyList = new ArrayList<>();
        AlarmHandlerFactory factory = new AlarmHandlerFactory(emptyList);

        // When & Then
        assertThatThrownBy(() -> {
            factory.getHandler(AlarmType.WEATHER_SUMMARY);})
                .isInstanceOf(IllegalArgumentException.class) // IllegalArgumentException 타입의 예외인지 확인
                .hasMessageContaining("지원하지 않는 알람 타입: " + AlarmType.WEATHER_SUMMARY); // 예외 메시지 내용 검증 (선택 사항)
    }

    private static class DummyAlarmHandler implements AlarmHandler {
        AlarmType alarmType;
        DummyAlarmHandler (AlarmType alarmType){
            this.alarmType = alarmType;
        }
        @Override
        public AlarmType getAlarmType() {
            return this.alarmType;
        }

        @Override
        public void handleRequest() {
        }

    }
}
