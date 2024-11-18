package org.pknu.weather.common;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.preview.dto.Request;
import org.pknu.weather.preview.service.PreviewService;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.pknu.weather.security.util.JWTUtil;
import org.pknu.weather.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

@SpringBootTest
@Slf4j
public class Dummy {
    @Autowired
    PreviewService previewService;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager em;

    @Autowired
    JWTUtil jwtUtil;

//    @Test
//    @Transactional
//    @Rollback(value = false)
//    void test1() {
//        String uuid = UUID.randomUUID().toString();
//        String email = "tester" + uuid.substring(uuid.length()-12, uuid.length()-1);
//        Long kakaoId = new Random().nextLong(99999999);
//
//        Optional<Member> appMember = memberService.findMemberByEmail(email);
//
//        String isNewMember = String.valueOf(appMember.isEmpty());
//        Member member = appMember.orElseGet(() -> memberService.saveMember(Member.builder().email(email).build()));
//
//        LoginMemberDTO loginMember = new LoginMemberDTO(member.getId(), member.getEmail());
//
//        Map<String, Object> claims = Map.of("id", loginMember.getId(),"email", loginMember.getEmail(),"kakaoId", kakaoId);
//
//        String accessToken = jwtUtil.generateToken(claims,30000);
//        log.info("ac############### " + accessToken);
//        String refreshToken = jwtUtil.generateToken(claims,30);
//
//        Map<String, String> tokens = Map.of("accessToken", accessToken,
//                "refreshToken", refreshToken,
//                "isNewMember", isNewMember);
//
//    }

    @Test
    @Transactional
    @Rollback(value = false)
    void test() {
        double lat = 35.14305906143902;
        double lon = 129.09816170902354;

        Location location = Location.builder()
                .point(GeometryUtils.getPoint(lat, lon))
                .latitude(lat)
                .longitude(lon)
                .province("부산광역시")
                .city("남구")
                .street("대연3동")
                .build();

        location = locationRepository.saveAndFlush(location);
        em.flush();
        em.clear();

        for(int i = 1; i <=123; i++) {
            Random random = new Random();

            Request.WeatherSurvey survey = Request.WeatherSurvey.builder()
                    .comment("코맨트")
                    .humidity(random.nextInt(2) == 0 ? "HUMID" : "COMMON_HUMID")
                    .skyCondition(
                            random.nextInt(4) == 0 ? "RAIN" :
                                    random.nextInt(4) == 1 ? "CLOUDY" :
                                            random.nextInt(4) == 2 ? "CLEAR_AND_CLOUDY" : "CLEAR"
                    )
                    .weatherSensitivity(
                            random.nextInt(3) == 0 ? "COLD" :
                                    random.nextInt(3) == 1 ? "HOT" : "NONE")
                    .todayFeelingTemperature(
                            random.nextInt(7) == 0 ? "VERY_COLD" :
                                    random.nextInt(7) == 1 ? "COLD" :
                                            random.nextInt(7) == 2 ? "LITTLE_COLD" :
                                                    random.nextInt(7) == 3 ? "COOL" :
                                                            random.nextInt(7) == 4 ? "COMMON" :
                                                                    random.nextInt(7) == 5 ? "WARM" : "LITTLE_WARM"
                    )
                    .windy(random.nextInt(2) == 0 ? "WINDY" : "NONE")
                    .build();

            previewService.createWeatherSurvey(survey);
        }

        Location loc = locationRepository.findLocationByFullAddress("부산광역시", "남구", "대연3동").get();

        for (int i = 0; i <= 23; i++) {
            LocalDateTime itime = LocalDateTime.of(LocalDate.now(), LocalTime.of(i, 0)).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime ttime = itime.plusDays(1);
            Random random = new Random();
            weatherRepository.save(Weather.builder()
                    .presentationTime(itime)
                    .location(loc)
                    .rain(0.0F)
                    .rainProb(0)
                    .temperature(random.nextInt(7, 16) + 1)
                    .humidity(50)
                    .windSpeed(1.5)
                    .build());

            weatherRepository.save(Weather.builder()
                    .presentationTime(ttime)
                    .location(loc)
                    .rain(0.0F)
                    .rainProb(0)
                    .temperature(random.nextInt(7, 16) + 1)
                    .humidity(50)
                    .windSpeed(1.5)
                    .build());
        }


    }
}
