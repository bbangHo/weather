package org.pknu.weather.config;

import org.pknu.weather.common.mapper.EnumTagMapper;
import org.pknu.weather.domain.tag.DustTag;
import org.pknu.weather.domain.tag.HumidityTag;
import org.pknu.weather.domain.tag.SkyTag;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.domain.tag.WindTag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Bean
    public EnumTagMapper enumTagConverter() {
        EnumTagMapper enumTagMapper = new EnumTagMapper();

        enumTagMapper.put(TemperatureTag.class);
        enumTagMapper.put(WindTag.class);
        enumTagMapper.put(HumidityTag.class);
        enumTagMapper.put(SkyTag.class);
        enumTagMapper.put(DustTag.class);

        return enumTagMapper;
    }
}
