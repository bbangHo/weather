package org.pknu.weather.security.oauth;


import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Component
@FeignClient(value = "login", url = "https://kapi.kakao.com")
public interface KaKaoLoginClient {

    @GetMapping("/v1/user/access_token_info")
    Response checkKakaoAccessToken(@RequestHeader("Authorization") String accessToken);

    @GetMapping("/v2/user/me")
    KakaoUserResponse getMemberData(@RequestHeader("Authorization") String accessToken,
                                    @RequestHeader("Content-type") String contentType
    );


}
