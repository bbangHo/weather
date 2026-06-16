package org.pknu.weather.weather.feignclient.weatherapi.exception;

public class ForecastNotAvailableException extends RuntimeException {

    public ForecastNotAvailableException(String message) {
        super(message);
    }
}
