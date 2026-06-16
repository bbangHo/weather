package org.pknu.weather.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.pknu.weather.weather.feignclient.weatherapi.exception.ForecastNotAvailableException;

@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate(
            @Value("${api.weather.retry.max-attempts:3}") int maxAttempts,
            @Value("${api.weather.retry.initial-interval-millis:300}") long initialIntervalMillis,
            @Value("${api.weather.retry.multiplier:2.0}") double multiplier,
            @Value("${api.weather.retry.max-interval-millis:2000}") long maxIntervalMillis
    ) {
        RetryTemplate retryTemplate = new RetryTemplate();

        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(ForecastNotAvailableException.class, false);
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxAttempts, retryableExceptions, true, true);

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(initialIntervalMillis);
        backOffPolicy.setMultiplier(multiplier);
        backOffPolicy.setMaxInterval(maxIntervalMillis);

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}
