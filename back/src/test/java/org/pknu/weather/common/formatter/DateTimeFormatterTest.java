package org.pknu.weather.common.formatter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

class DateTimeFormatterTest {

    @ParameterizedTest
    @CsvSource({
            "2024-12-13T23:10, 2024-12-13T23:00",
            "2024-12-14T00:10, 2024-12-13T23:00",
            "2024-12-14T01:10, 2024-12-13T23:00",
            "2024-12-14T02:10, 2024-12-14T02:00",
            "2024-12-14T03:10, 2024-12-14T02:00",
            "2024-12-14T04:10, 2024-12-14T02:00",
    })
    void 날짜가_변경되는_시점에서도_baseTime을_정상적으로_반환하는지_테스트(String dateTimeString, String dateTimeStringResult) {
        // given
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime localDateTimeResult = LocalDateTime.parse(dateTimeStringResult, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // when
        LocalDateTime baseTimeCloseToNow = DateTimeFormatter.getBaseTimeCloseToNow(localDateTime);

        // then
        Assertions.assertThat(baseTimeCloseToNow).isEqualTo(localDateTimeResult);
    }
}