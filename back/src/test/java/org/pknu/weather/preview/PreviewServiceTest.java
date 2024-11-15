package org.pknu.weather.preview;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.preview.dto.Request.WeatherSurvey;
import org.pknu.weather.preview.service.PreviewService;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class PreviewServiceTest {
    @Autowired
    PreviewService previewService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    @Rollback(value = false)
    void test() {
        Location location = Location.builder()
                .point(GeometryUtils.getPoint(35.1316361111111, 129.102577777777))
                .latitude(35.1316361111111)
                .longitude(129.102577777777)
                .province("부산광역시")
                .city("남구")
                .street("대연3동")
                .build();

        location = locationRepository.save(location);

        WeatherSurvey survey = WeatherSurvey.builder()
                .gender("남자")
                .weatherSensitivity("HOT")
                .todayFeelingTemperature("COLD")
                .skyCondition("CLEAR")
                .humidity("COMMON_HUMID")
                .windy("NONE")
                .comment("test")
                .build();

        previewService.createWeatherSurvey(survey);

        List<Member> memberList = memberRepository.findAll();
        List<Post> postList = postRepository.findAll();
        List<Tag> tagList = tagRepository.findAll();
        List<Location> locationList = locationRepository.findAll();

        Assertions.assertThat(memberList.size()).isEqualTo(1);
        Assertions.assertThat(postList.size()).isEqualTo(1);
        Assertions.assertThat(tagList.size()).isEqualTo(1);
        Assertions.assertThat(locationList.size()).isEqualTo(1);

    }
}