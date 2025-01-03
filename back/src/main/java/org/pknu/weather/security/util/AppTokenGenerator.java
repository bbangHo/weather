package org.pknu.weather.security.util;

import com.google.gson.Gson;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Member;
import org.pknu.weather.service.MemberService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppTokenGenerator {
    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    public Map<String, Object> getUserInfo(UserInfoStrategy userInfoStrategy, String token) {
        return userInfoStrategy.getUserInfo(token);
    }

    public Map<String, String> generateAppToken(Map<String, Object> userInfo) {

        log.debug("generateAppToken start----------------------------------------");
        String email = (String)userInfo.get("email");

        String isNewMember = saveMember(email);

        log.debug("Generate AppToken ...................");
        String accessToken = jwtUtil.generateToken(userInfo,3);
        String refreshToken = jwtUtil.generateToken(userInfo,30);

        return Map.of("accessToken", accessToken,
                "refreshToken", refreshToken,
                "isNewMember", isNewMember);

    }

    private String saveMember(String email) {
        Optional<Member> appMember = memberService.findMemberByEmail(email);
        appMember.orElseGet(() -> memberService.saveMember(Member.builder().email(email).build()));

        return String.valueOf(appMember.isEmpty());
    }
}
