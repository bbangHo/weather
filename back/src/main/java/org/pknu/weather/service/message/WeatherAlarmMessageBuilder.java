package org.pknu.weather.service.message;

import java.util.ArrayList;
import java.util.List;

public class WeatherAlarmMessageBuilder {
    private String rainStatus;
    private Integer maxTemp;
    private Integer minTemp;
    private Integer pm10;
    private String maxUvTime;
    private Integer maxUvValue;


    public WeatherAlarmMessageBuilder withRainStatus(String rainStatus) {
        this.rainStatus = rainStatus;
        return this;
    }

    public WeatherAlarmMessageBuilder withTemperature(Integer maxTemp, Integer minTemp) {
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        return this;
    }

    public WeatherAlarmMessageBuilder withDust(Integer pm10) {
        this.pm10 = pm10;
        return this;
    }

    public WeatherAlarmMessageBuilder withUV(String maxUvTime, Integer maxUvValue) {
        this.maxUvTime = maxUvTime;
        this.maxUvValue = maxUvValue;
        return this;
    }

    public String build() {
        List<String> messageParts = new ArrayList<>();

        appendTemperature(messageParts);
        appendRainStatus(messageParts);
        appendDustLevel(messageParts);
        appendUvLevel(messageParts);

        return formatMessage(messageParts);
    }

    private void appendTemperature(List<String> messageParts) {
        if (maxTemp != null && minTemp != null)
            messageParts.add("🌡️ " + String.format("%-24s", "기온: " + minTemp + "°C / " + maxTemp + "°C"));
    }

    private void appendRainStatus(List<String> messageParts) {
        if (rainStatus != null)
            messageParts.add("☔️ " + String.format("%-24s", "강수 상태: " + rainStatus));
    }

    private void appendDustLevel(List<String> messageParts) {
        if (pm10 != null)
            messageParts.add("🌫️ " + String.format("%-24s", "미세먼지: " + getDustLevel(pm10)));
    }

    private void appendUvLevel(List<String> messageParts) {
        if (maxUvTime != null && maxUvValue != null)
            messageParts.add("🌞 " + String.format("%-24s", "자외선: " + getUvLevel(maxUvValue) + " - " + maxUvTime + "시"));
    }

    private String formatMessage(List<String> messageParts) {
        StringBuilder alarmMessage = new StringBuilder();

        for (int i = 0; i < messageParts.size(); i++) {

            alarmMessage.append(messageParts.get(i));

            if ((i < messageParts.size() - 1) && (i % 2 == 1)) {
                alarmMessage.append("\n");
            }
        }

        return alarmMessage.toString().trim();
    }

    private String getDustLevel(int pm10) {
        if (pm10 == 1) return "좋음";
        if (pm10 == 2) return "보통";
        if (pm10 == 3) return "나쁨";
        return "매우 나쁨";
    }

    private String getUvLevel(int uv) {
        if (uv == 1) return "좋음";
        if (uv == 2) return "보통";
        if (uv == 3) return "나쁨";
        return "매우 나쁨";
    }
}