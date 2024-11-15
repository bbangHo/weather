package org.pknu.weather.preview.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.tag.DustTag;
import org.pknu.weather.domain.tag.HumidityTag;
import org.pknu.weather.domain.tag.SkyTag;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.domain.tag.WindTag;
import org.pknu.weather.preview.dto.Request.WeatherSurvey;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
