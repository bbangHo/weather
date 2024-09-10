package org.pknu.weather.repository;

import org.junit.jupiter.api.Test;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.tag.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootTest
class TagRepositoryTest {
    @Autowired
    TagRepository tagRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

//    @BeforeEach
//    @Transactional
    void init() {
        String[][] list = {
                {"서울특별시", "종로구", "청운효자동"},
                {"서울특별시", "종로구", "사직동"},
                {"서울특별시", "종로구", "삼청동"},
                {"서울특별시", "종로구", "부암동"},
                {"부산광역시", "부산진구", "부전제2동"},
                {"부산광역시", "부산진구", "연지동"},
                {"부산광역시", "부산진구", "초읍동"},
                {"부산광역시", "부산진구", "양정제1동"}
        };

        double[][] latlngList = {
                {126.9706519, 37.5841367},
                {126.970955555555, 37.5732694444444},
                {126.983977777777, 37.582425},
                {126.966444444444, 37.5898555555555},
                {129.059075, 35.1495222222222},
                {129.055008333333, 35.1697138888888},
                {129.049833333333, 35.175625},
                {129.066655555555, 35.1713972222222}
        };

        for (int i = 0; i < list.length; i++) {
            double latitude = latlngList[i][1];
            double longitude = latlngList[i][0];

            Location location = Location.builder()
                    .point(GeometryUtils.getPoint(latitude, longitude))
                    .province(list[i][0])
                    .city(list[i][1])
                    .street(list[i][2])
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();

            Member member = Member.builder()
                    .location(location)
                    .email(i + "email@naver.com")
                    .nickname(i + "user")
                    .sensitivity(Sensitivity.NONE)
                    .build();

            memberRepository.save(member);

            for(int j = 1; j <= 100; j++) {
                Random random = new Random();

                Post post = Post.builder()
                        .content("content")
                        .member(member)
                        .location(location)
                        .build();

                postRepository.save(post);

                Tag tag = Tag.builder()
                        .location(location)
                        .temperTag(
                                (TemperatureTag) Arrays.stream(TemperatureTag.values())
                                        .map(t -> t.findByCode(random.nextInt(10) + 1))
                                        .findAny()
                                        .orElse(TemperatureTag.COMMON)
                        )
                        .windTag(
                                (WindTag) Arrays.stream(WindTag.values())
                                        .map(t -> t.findByCode(random.nextInt(3) + 1))
                                        .findAny()
                                        .orElse(WindTag.NONE)
                        )
                        .humidityTag(
                                (HumidityTag) Arrays.stream(HumidityTag.values())
                                        .map(t -> t.findByCode(random.nextInt(5) + 1))
                                        .findAny()
                                        .orElse(HumidityTag.COMMON_HUMID)
                        )
                        .skyTag(
                                (SkyTag) Arrays.stream(SkyTag.values())
                                        .map(t -> t.findByCode(random.nextInt(5) + 1))
                                        .findAny()
                                        .orElse(SkyTag.CLEAR)
                        )
                        .dustTag(
                                (DustTag) Arrays.stream(DustTag.values())
                                        .map(t -> t.findByCode(random.nextInt(5) + 1))
                                        .findAny()
                                        .orElse(DustTag.NORMAL)
                        )
                        .build();

                tag = tagRepository.save(tag);
            }
        }
    }

    @Test
    @Transactional
    void test() {
        // given
        Member member = memberRepository.findAll().get(0);
        Location location = member.getLocation();
        List<Tag> tagList = tagRepository.findAll();

        // when
//        Map<EnumTag, Long> tagQueryResultMap = tagRepository.rankingTags(location);

        // then
//        System.out.println(tagQueryResultMap.getHumidityCount());
//        assertThat(tagQueryResult.getQueryDustTag()).isEqualTo(400);
    }
}