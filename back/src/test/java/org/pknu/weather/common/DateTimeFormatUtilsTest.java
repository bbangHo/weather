package org.pknu.weather.common;

import org.junit.jupiter.api.Test;

class DateTimeFormatUtilsTest {

    @Test
    void DateTimeFormatUtils_정상_작동_테스트() {
        //given
        String formattedDate2YYMMDD = DateTimeFormaterUtils.getFormattedDate();
        String formattedTime2HHMM = DateTimeFormaterUtils.getFormattedTimeByThreeHour();
    }
}