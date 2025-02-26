package org.pknu.weather.security.util.logout;

import feign.Response;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.feignClient.AppleAuthClient;
import org.pknu.weather.security.dto.AppleAuthTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@RequiredArgsConstructor
@Component
@Slf4j
public class AppleUnlinker implements UserUnlinker {

    private final AppleAuthClient appleAuthClient;

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key.id}")
    private String keyId;

    @Value("${apple.key.content}")
    private String key;

    @Override
    public void unlinkUser(String userInfo) {

        AppleAuthTokenResponse appleAuthToken = null;

        try {
            appleAuthToken = GenerateAuthToken(userInfo);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        if (appleAuthToken.getAccessToken() != null) {
            try {
                Response result = appleAuthClient.revokeToken("com.capstone.weatherapp", createClientSecret(), appleAuthToken.getAccessToken());
                log.info("애플 로그아웃 완료" + String.valueOf(result.status()));
            } catch (Exception e){
                log.info(e.getMessage());
                /*
                예외 추가

                 */
            }
        }
    }

    public String createClientSecret() throws NoSuchAlgorithmException, InvalidKeySpecException {

        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());

        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", keyId);
        jwtHeader.put("alg", "ES256");

        return Jwts.builder()
                .setHeaderParams(jwtHeader)
                .setIssuer(teamId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .setAudience("https://appleid.apple.com")
                .setSubject(clientId)
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {

        String base64PrivateKey = key;

        byte[] encoded = Base64.getDecoder().decode(base64PrivateKey);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }

    public AppleAuthTokenResponse GenerateAuthToken(String token)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            return appleAuthClient.getAccessToken(token, clientId, createClientSecret(),"authorization_code");
        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString());
            throw new IllegalArgumentException("Apple Auth Access Token Error");
        }
    }

}
