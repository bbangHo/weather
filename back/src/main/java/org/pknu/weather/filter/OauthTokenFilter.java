package org.pknu.weather.filter;

import com.google.gson.Gson;
import feign.FeignException;
import feign.Response;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.security.exception.TokenException;
import org.pknu.weather.security.oauth.KakaoUserResponse;
import org.pknu.weather.security.oauth.KaKaoLoginClient;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class OauthTokenFilter extends OncePerRequestFilter {

    private final KaKaoLoginClient kaKaoLoginClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("OauthToken Filter start.................");

        Map<String, String> jsonData = parseRequestJSON(request);

        String accessToken = jsonData.get("accessToken");

        String authHeader = "Bearer " + accessToken;

        try {
            Response result = kaKaoLoginClient.checkKakaoAccessToken(authHeader);
            KakaoUserResponse oauthMember = kaKaoLoginClient.getMemberData(authHeader, "application/x-www-form-urlencoded;charset=utf-8");

            request.setAttribute("email", oauthMember.getKakao_account().getEmail());

        } catch (FeignException e){
            /**
             * 에러 처리 - 로그로 출력하기
             */
            log.error(e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);

    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) throws TokenException {
        Map<String, String> loginData;

        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            Gson gson = new Gson();
            loginData = gson.fromJson(reader, Map.class);
        } catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException();
        }

        if(!loginData.containsKey("accessToken")){
            log.info("test................");
            throw new TokenException(ErrorStatus.MALFORMED_LOGIN_DATA);
        }

        return loginData;
    }

}
