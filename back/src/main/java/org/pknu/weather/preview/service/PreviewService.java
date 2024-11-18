package org.pknu.weather.preview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.*;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.tag.*;
import org.pknu.weather.preview.dto.Request.WeatherSurvey;
import org.pknu.weather.preview.dto.Response.TagHour;
import org.pknu.weather.preview.dto.Response.TimeAndTemp;
import org.pknu.weather.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PreviewService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final LocationRepository locationRepository;
    private final TagRepository tagRepository;
    private final WeatherRepository weatherRepository;

    public List<Member> getMostMembers() {
        Location location = locationRepository.findLocationByFullAddress("부산광역시", "남구", "대연3동").get();
        return memberRepository.findAll().stream()
                .filter(m -> {
                    return m.getLocation().equals(location);
                }).toList();
    }

    public Integer getSharedWeatherIncrease() {
        Location location = locationRepository.findLocationByFullAddress("부산광역시", "남구", "대연3동").get();
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        LocalDateTime now = LocalDateTime.now();

        return (int) postRepository.findByLocation(location).stream()
                .filter(post -> !post.getCreatedAt().isBefore(oneHourAgo) && !post.getCreatedAt().isAfter(now))
                .count();
    }

    public Integer getSharedWeatherCount() {
        Location location = locationRepository.findLocationByFullAddress("부산광역시", "남구", "대연3동").get();
        return postRepository.findByLocation(location).size();
    }

    public Map<TemperatureTag, Long> getMostSelectedTag() {
        Location location = locationRepository.findLocationByFullAddress("부산광역시", "남구", "대연3동").get();
        Map<TemperatureTag, Long> collect = location.getTagList().stream()
                .collect(Collectors.groupingBy(Tag::getTemperTag, Collectors.counting()));

        return collect;
    }

    public String createWeatherSurvey(WeatherSurvey survey) {
        String uuid = UUID.randomUUID().toString();
        String uuidNickname = uuid.substring(uuid.length() - 12 - 1, uuid.length() - 1);

        Location location = locationRepository.findLocationByFullAddress("부산광역시", "남구", "대연3동").get();

        Member member = Member.builder()
                .email("user" + uuid + "@gmail.com")
                .sensitivity(Sensitivity.valueOf(survey.getWeatherSensitivity()))
                .nickname("날씨 요정" + uuidNickname)
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
    public List<TagHour> getTags(Integer sensitivity) {
        Location location = locationRepository.findLocationByFullAddress("부산광역시", "남구", "대연3동").get();
        List<Weather> weatherList = location.getWeatherList();
        return location.getTagList().stream()
                .map(t -> {
                    return TagHour.builder()
                            .temperatureTag(t.getTemperTag())
                            .time(t.getCreatedAt().format(DateTimeFormatter.ofPattern("HH")))
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimeAndTemp> getTimeAndTemp() {
        Location location = locationRepository.findLocationByFullAddress("부산광역시", "남구", "대연3동").get();
        return weatherRepository.getTemperatureForHour(LocalDateTime.now()).stream()
                .map((Weather w) -> TimeAndTemp.builder()
                        .time(w.getPresentationTime())
                        .temp(w.getTemperature())
                        .build())
                .toList();

    }

}
