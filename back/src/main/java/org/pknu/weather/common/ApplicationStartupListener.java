package org.pknu.weather.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private final String jwtKey;

    public ApplicationStartupListener(@Value("${spring.jwt.key}") String jwtKey) {
        this.jwtKey = jwtKey;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        TokenConverter.setKey(jwtKey);
    }
}