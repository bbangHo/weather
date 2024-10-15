package org.pknu.weather.domain;

import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.dto.MemberJoinDTO;
import org.pknu.weather.dto.WeatherResponse;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExtraWeather extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "extra_weather_id")
    private Long id;

    private LocalDateTime basetime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    private Integer uv;
    private Integer o3;
    private Integer pm10;
    private Integer pm25;

    public void updateExtraWeather (WeatherResponse.ExtraWeatherInfo extraWeatherInfo){
        this.basetime = extraWeatherInfo.getBaseTime();
        if (extraWeatherInfo.getUvGrade() != null)
            this.uv = extraWeatherInfo.getUvGrade();

        if (extraWeatherInfo.getO3Grade() != null)
            this.o3 = extraWeatherInfo.getO3Grade();

        if (extraWeatherInfo.getPm10Grade() != null)
            this.pm10 = extraWeatherInfo.getPm10Grade();

        if (extraWeatherInfo.getPm25Grade() != null)
            this.pm25 = extraWeatherInfo.getPm25Grade();
    }
}
