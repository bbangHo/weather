package org.pknu.weather.common.formatter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public final class DateTimeFormatter {

    /**
     * post의 생성 시각과 현재와의 차이를 String으로 반환합니다.
     *
     * @param createdAt post의 생성 시각
     * @return
     */
    public static String pastTimeToString(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime comparison = createdAt;
        Duration diff = Duration.between(createdAt, now);

        if (comparison.plusDays(1).isBefore(now)) {
            return diff.toDays() + "일 전";
        } else if (comparison.plusHours(1).isBefore(now)) {
            return diff.toHours() + "시간 전";
        } else {
            return diff.toMinutes() + "분 전";
        }
    }

    /**
     * LocalDate를 yyyyMMdd 형태로 반환합니다.
     *
     * @return yyyyMMdd, String 형태의 formatted date
     */
    public static String getFormattedDate() {
        LocalDate currentDate = LocalDate.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
        return currentDate.format(formatter);
    }

    public static String getFormattedDate(LocalDate currentDate) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
        return currentDate.format(formatter);
    }

    /**
     * LocalTime을 HHmm 형태로 반환합니다. 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 중 현재보다 과거이면서 가장가까운 값을 반환합니다.
     *
     * @return HHmm, String 형태의 formatted time
     */
    public static String getFormattedTimeByThreeHour() {
        LocalDateTime currentLocalDateTime = getBaseTimeCloseToNow();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HHmm");
        return currentLocalDateTime.format(formatter);
    }

    /**
     * LocalTime을 HHmm 형태로 반환합니다. 1시간 단위로 반환합니다.
     *
     * @return HHmm, String 형태의 formatted time
     */
    public static String getFormattedTimeByOneHour() {
        LocalTime currentTime = LocalTime.now().withMinute(0);
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HHmm");
        return currentTime.format(formatter);
    }

    /**
     * LocalTime을 HHmm 형태로 반환합니다. 1시간 단위로 반환합니다.
     *
     * @return HHmm, String 형태의 formatted time
     */
    public static String getFormattedTimeByOneHour(LocalTime now) {
        LocalTime currentTime = now.withMinute(0);
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HHmm");
        return currentTime.format(formatter);
    }

    /**
     * yyyyMMDD HHmm 을 LocalDateTime으로 변경
     *
     * @param date
     * @param time
     * @return
     */
    public static LocalDateTime formattedDateTime2LocalDateTime(String date, String time) {
        String dateTime = date + time;
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        return LocalDateTime.parse(dateTime, formatter);
    }

    /**
     * 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 시 중 현재보다 과거이면서 가장가까운 값을 반환합니다.
     *
     * @return LocalDateTime
     */
    public static LocalDateTime getBaseTimeCloseToNow() {
        LocalDateTime currentLocalDateTime = LocalDateTime.now();
        return findBaseTime(currentLocalDateTime);
    }

    public static LocalDateTime getBaseTimeCloseToNow(LocalDateTime currentLocalDateTime) {
        return findBaseTime(currentLocalDateTime);
    }

    private static LocalDateTime findBaseTime(LocalDateTime currentLocalDateTime) {
        List<LocalDateTime> predefinedTimes = getPredefinedTimes(currentLocalDateTime);

        LocalDateTime closestPastTime = predefinedTimes.get(0);
        for (LocalDateTime time : predefinedTimes) {
            if (time.isBefore(currentLocalDateTime)) {
                closestPastTime = time;
            } else {
                break;
            }
        }

        return closestPastTime;
    }

    private static List<LocalDateTime> getPredefinedTimes(LocalDateTime currentLocalDateTime) {
        LocalDate currentLocalDate = currentLocalDateTime.toLocalDate();

        return Arrays.asList(
                LocalDateTime.of(currentLocalDate.minusDays(1), LocalTime.of(23, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(2, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(5, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(8, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(11, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(14, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(17, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(20, 0)),
                LocalDateTime.of(currentLocalDate, LocalTime.of(23, 0))
        );
    }
}
