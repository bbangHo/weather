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
            checkToken(authHeader);
        } catch (TokenException tokenException){
            tokenException.sendResponseError(response);
            return;
        }

        try {
            KakaoUserResponse oauthMember = kaKaoLoginClient.getMemberData(authHeader, "application/x-www-form-urlencoded;charset=utf-8");

            request.setAttribute("email", oauthMember.getKakao_account().getEmail());

        } catch (FeignException e){
            log.error(e.getMessage());
            throw e;
        }

        filterChain.doFilter(request, response);

    }

    private void checkToken(String authHeader) throws TokenException{
        Response result = kaKaoLoginClient.checkKakaoAccessToken(authHeader);
        int status = result.status();
        switch (status){
            case 1:
                throw new TokenException(ErrorStatus.KAKAO_SERVER_ERROR);
            case 2:
                throw new TokenException(ErrorStatus.KAKAO_ACCESS_TOKEN_BAT_TYPE);
            case 401:
                throw new TokenException(ErrorStatus.MALFORMED_KAKAO_ACCESS_TOKEN);
            case 200:
                break;
            default:
                throw new RuntimeException();
        }
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
            throw new TokenException(ErrorStatus.ACCESS_TOKEN_NOT_ACCEPTED);
        }

        return loginData;
    }

}
