//package org.pknu.weather.repository;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.pknu.weather.common.utils.GeometryUtils;
//import org.pknu.weather.domain.Location;
//import org.pknu.weather.domain.Member;
//import org.pknu.weather.domain.Post;
//import org.pknu.weather.domain.Tag;
//import org.pknu.weather.domain.common.Sensitivity;
//import org.pknu.weather.domain.tag.*;
//import org.pknu.weather.dto.TagQueryResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//class TagRepositoryTest {
//    @Autowired
//    TagRepository tagRepository;
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Autowired
//    PostRepository postRepository;
//
//    @BeforeEach
//    @Transactional
//    void init() {
//        String[][] list = {
//                {"서울특별시", "종로구", "청운효자동"}
//        };
//
//        double[][] latlngList = {
//                {126.9706519, 37.5841367}
//        };
//
//        for (int i = 0; i < list.length; i++) {
//            double latitude = latlngList[i][1];
//            double longitude = latlngList[i][0];
//
//            Location location = Location.builder()
//                    .point(GeometryUtils.getPoint(latitude, longitude))
//                    .province(list[i][0])
//                    .city(list[i][1])
//                    .street(list[i][2])
//                    .latitude(latitude)
//                    .longitude(longitude)
//                    .build();
//
//            Member member = Member.builder()
//                    .location(location)
//                    .email(i + "email@naver.com")
//                    .nickname(i + "user")
//                    .sensitivity(Sensitivity.NONE)
//                    .build();
//
//            memberRepository.save(member);
//
//            for(int j = 1; j <= 10; j++) {
//                Random random = new Random();
//
//                Post post = Post.builder()
//                        .content("content")
//                        .member(member)
//                        .location(location)
//                        .build();
//
//                postRepository.save(post);
//
//                Tag tag = Tag.builder()
//                        .location(location)
//                        .temperTag(
//                                (TemperatureTag) Arrays.stream(TemperatureTag.values())
//                                        .map(t -> t.findByCode(random.nextInt(TemperatureTag.values().length) + 1))
//                                        .findAny()
//                                        .orElse(TemperatureTag.COMMON)
//                        )
//                        .windTag(
//                                (WindTag) Arrays.stream(WindTag.values())
//                                        .map(t -> t.findByCode(random.nextInt(WindTag.values().length) + 1))
//                                        .findAny()
//                                        .orElse(WindTag.NONE)
//                        )
//                        .humidityTag(
//                                (HumidityTag) Arrays.stream(HumidityTag.values())
//                                        .map(t -> t.findByCode(random.nextInt(HumidityTag.values().length) + 1))
//                                        .findAny()
//                                        .orElse(HumidityTag.COMMON_HUMID)
//                        )
//                        .skyTag(
//                                (SkyTag) Arrays.stream(SkyTag.values())
//                                        .map(t -> t.findByCode(random.nextInt(SkyTag.values().length) + 1))
//                                        .findAny()
//                                        .orElse(SkyTag.CLEAR)
//                        )
//                        .dustTag(
//                                (DustTag) Arrays.stream(DustTag.values())
//                                        .map(t -> t.findByCode(random.nextInt(DustTag.values().length) + 1))
//                                        .findAny()
//                                        .orElse(DustTag.NORMAL)
//                        )
//                        .build();
//
//                tag = tagRepository.save(tag);
//            }
//
//            for(int j = 1; j <= 10; j++) {
//                Tag tag = Tag.builder()
//                        .location(location)
//                        .temperTag(TemperatureTag.COMMON)
//                        .windTag(WindTag.NONE)
//                        .humidityTag(HumidityTag.DRY)
//                        .skyTag(SkyTag.CLEAR)
//                        .dustTag(DustTag.VERY_GOOD)
//                        .build();
//
//                tag = tagRepository.save(tag);
//            }
//        }
//    }
//
//    @Test
//    @Transactional
//    void test() {
//        // given
//        Member member = memberRepository.findAll().get(0);
//        Location location = member.getLocation();
//        List<Tag> tagList = tagRepository.findAll();
//
//        // when
//        List<TagQueryResult> tagQueryResults = tagRepository.rankingTags(location);
//
//        // then
//        assertThat(tagQueryResults.get()).isEqualTo(400);
//    }
//}