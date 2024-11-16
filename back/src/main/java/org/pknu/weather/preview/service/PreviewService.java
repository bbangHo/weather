package org.pknu.weather.preview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.*;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.tag.*;
import org.pknu.weather.preview.dto.Request.WeatherSurvey;
import org.pknu.weather.preview.dto.Response;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PreviewService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final LocationRepository locationRepository;
    private final TagRepository tagRepository;

    public String createWeatherSurvey(WeatherSurvey survey) {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.substring(uuid.length() - 12 - 1, uuid.length() - 1);

        Location location = locationRepository.findLocationByFullAddress("부산광역시", "남구", "대연3동").get();

        Member member = Member.builder()
                .email("user-" + uuid + "@gmail.com")
                .sensitivity(Sensitivity.valueOf(survey.getWeatherSensitivity()))
                .nickname("날씨 요정-" + uuid)
                .profileImage(null)
                .location(location)
                .build();

        member = memberRepository.save(member);

        Post post = Post.builder()
                .member(member)
                .location(location)
                .content(survey.getComment())
                .build();

        post = postRepository.save(post);

        Tag tag = Tag.builder()
                .location(location)
                .post(post)
                .temperTag(TemperatureTag.valueOf(survey.getTodayFeelingTemperature()))
                .skyTag(SkyTag.valueOf(survey.getSkyCondition()))
                .humidityTag(HumidityTag.valueOf(survey.getHumidity()))
                .windTag(WindTag.valueOf(survey.getWindy()))
                .dustTag(DustTag.NORMAL)
                .build();

        tagRepository.save(tag);

        return "Thank you";
    }

    @Transactional(readOnly = true)
    public void getTags() {
        Location location = locationRepository.findLocationByFullAddress("부산광역시", "남구", "대연3동").get();
        List<Weather> weatherList = location.getWeatherList();

        // 08시 ~ 15시 시간대 온도 가져오기
        for (Weather weather : weatherList) {
            LocalDateTime time8 = LocalDateTime.now()
                    .withHour(9)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            LocalDateTime time15 = time8.plusHours(6);

            LocalDateTime presentationTime = weather.getPresentationTime();

            if(presentationTime.isAfter(time8) &&  presentationTime.isBefore(time15)) {
                Response.TimeAndTemp timeAndTemp = Response.TimeAndTemp.builder()
                        .time(weather.getPresentationTime())
                        .temp(weather.getTemperature())
                        .build();
            }
        }
    }

}
