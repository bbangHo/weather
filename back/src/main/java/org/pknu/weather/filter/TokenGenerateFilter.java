package org.pknu.weather.filter;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.domain.Member;
import org.pknu.weather.security.dto.LoginMemberDTO;
import org.pknu.weather.security.util.JWTUtil;
import org.pknu.weather.service.MemberService;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class TokenGenerateFilter extends OncePerRequestFilter {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("TokenGenerateFilter start----------------------------------------");
        String email = (String)request.getAttribute("email");
        Long kakaoId = (Long) request.getAttribute("kakaoId");

        Optional<Member> appMember = memberService.findMemberByEmail(email);

        String isNewMember = String.valueOf(appMember.isEmpty());
        Member member = appMember.orElseGet(() -> memberService.saveMember(Member.builder().email(email).build()));


        LoginMemberDTO loginMember = new LoginMemberDTO(member.getId(), member.getEmail());

        log.info("Generate AppToken ...................");

        Map<String, Object> claims = Map.of("id", loginMember.getId(),"email", loginMember.getEmail(),"kakaoId", kakaoId);


        String accessToken = jwtUtil.generateToken(claims,3);
        String refreshToken = jwtUtil.generateToken(claims,30);

        Map<String, String> tokens = Map.of("accessToken", accessToken,
                "refreshToken", refreshToken,
                "isNewMember", isNewMember);

        Gson gson = new Gson();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        String responseStr = gson.toJson(ApiResponse.onSuccess(tokens));
        response.getWriter().println(responseStr);
    }

}
