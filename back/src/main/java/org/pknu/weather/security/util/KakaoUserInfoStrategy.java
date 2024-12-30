package org.pknu.weather.security.util;

import com.google.gson.Gson;
import feign.FeignException;
import feign.Response;
import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.feignClient.KaKaoLoginClient;
import org.pknu.weather.security.dto.KakaoUserInfo;
import org.pknu.weather.security.dto.KakaoUserResponseDTO;
import org.pknu.weather.security.exception.TokenException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoUserInfoStrategy implements UserInfoStrategy{

    private final KaKaoLoginClient kaKaoLoginClient;

    @Override
    public Map<String, Object> getUserInfo(String accessToken) throws TokenException{
        log.debug("KakaoUserInfoStrategy start.................");

        String authHeader = "Bearer " + accessToken;

        checkToken(authHeader);

        KakaoUserResponseDTO kakaoUser = kaKaoLoginClient.getMemberData(authHeader, "application/x-www-form-urlencoded;charset=utf-8");
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo("kakao", kakaoUser.getKakao_account().getEmail(), kakaoUser.getId());

        return kakaoUserInfo.getUserInfo();
    }

    private void checkToken(String authHeader) throws TokenException{
        Response result = kaKaoLoginClient.checkKakaoAccessToken(authHeader);
        int status = result.status();
        switch (status){
            case 200:
                break;
            case 1:
                throw new TokenException(ErrorStatus.KAKAO_SERVER_ERROR);
            case 2:
                throw new TokenException(ErrorStatus.KAKAO_ACCESS_TOKEN_BAT_TYPE);
            case 401:
                throw new TokenException(ErrorStatus.MALFORMED_KAKAO_ACCESS_TOKEN);
            default:
                throw new RuntimeException();
        }
    }

}
