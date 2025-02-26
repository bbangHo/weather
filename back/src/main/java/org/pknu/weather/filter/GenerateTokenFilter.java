package org.pknu.weather.filter;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.security.dto.SocialUserInfo;
import org.pknu.weather.security.exception.TokenException;
import org.pknu.weather.security.util.AppTokenGenerator;
import org.pknu.weather.security.util.signup.AppleUserInfoStrategy;
import org.pknu.weather.security.util.signup.KakaoUserInfoStrategy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class GenerateTokenFilter extends OncePerRequestFilter {
    private final AppTokenGenerator appTokenGenerator;
    private final KakaoUserInfoStrategy kakaoUserInfoStrategy;
    private final AppleUserInfoStrategy appleUserInfoStrategy;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

        log.debug("GenerateToken Filter start.................");

        Map<String, String> jsonData = parseRequestJSON(request);
        String accessToken = jsonData.get("accessToken");
        String type = jsonData.get("type");

        if(type == null || type.isBlank()) {
            sendTypeError(response);
            return;
        }

        SocialUserInfo userInfo = null;

        if (type.equals("kakao"))
            userInfo = appTokenGenerator.getUserInfo(kakaoUserInfoStrategy, accessToken);
        else if (type.equals("apple"))
            userInfo = appTokenGenerator.getUserInfo(appleUserInfoStrategy, accessToken);
        else {
            sendTypeError(response);
            return;
        }

        Map<String, String> appToken = getAppToken(userInfo);

        sendToken(response, appToken);
    }

    private void sendTypeError(HttpServletResponse response) {
        log.error("social 로그인의 타입에 문제가 발생했습니다.");
        new TokenException(ErrorStatus.TYPE_NOT_ACCEPTED).sendResponseError(response);
    }

    private Map<String, String> getAppToken(SocialUserInfo userInfo) {

        return appTokenGenerator.generateAppToken(userInfo.getUserInfo());

    }

    private void sendToken(HttpServletResponse response, Map<String, String> appToken) throws IOException {
        Gson gson = new Gson();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        String responseStr = gson.toJson(ApiResponse.onSuccess(appToken));
        response.getWriter().println(responseStr);
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
            throw new TokenException(ErrorStatus.ACCESS_TOKEN_NOT_ACCEPTED);
        }

        return loginData;
    }

}
