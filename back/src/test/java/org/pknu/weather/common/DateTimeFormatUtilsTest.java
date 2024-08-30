package org.pknu.weather.common;

import org.junit.jupiter.api.Test;
import org.pknu.weather.common.formatter.DateTimeFormatter;

class DateTimeFormatUtilsTest {

    @Test
    void DateTimeFormatUtils_정상_작동_테스트() {
        //given
        String formattedDate2YYMMDD = DateTimeFormatter.getFormattedDate();
        String formattedTime2HHMM = DateTimeFormatter.getFormattedTimeByThreeHour();
    }
}