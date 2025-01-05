package org.pknu.weather.security.util.signup;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.feignClient.AppleAuthClient;
import org.pknu.weather.security.dto.SocialUserInfo;
import org.pknu.weather.security.util.ApplePublicKeyGenerator;
import org.pknu.weather.security.util.JWTUtil;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleUserInfoStrategy implements UserInfoStrategy {
    private final JWTUtil jwtUtil;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final AppleAuthClient appleAuthClient;
    @Override
    public SocialUserInfo getUserInfo(String identityToken) {
        log.debug("AppleUserInfoStrategy start.................");

        Map<String, String> headers = parseHeader(identityToken);

        PublicKey applePublicKey = getPublicKey(headers);

        Claims tokenClaims = jwtUtil.getTokenClaims(identityToken, applePublicKey);

        return new SocialUserInfo("apple", String.valueOf(tokenClaims.get("email")));
    }

    private PublicKey getPublicKey(Map<String, String> headers) {
        PublicKey publicKey;
        try {
            publicKey = applePublicKeyGenerator.generatePublicKey(headers, appleAuthClient.getAppleAuthPublicKey());

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return publicKey;
    }

    private Map<String, String> parseHeader(String identityToken) {
        Map<String, String> headers;
        try {
            headers = jwtUtil.parseHeaders(identityToken);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error(jsonProcessingException.getMessage());
            throw new GeneralException(ErrorStatus.MALFORMED_APPLE_TOKEN);
        }
        return headers;
    }
}
