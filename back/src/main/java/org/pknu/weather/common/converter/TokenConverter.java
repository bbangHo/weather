package org.pknu.weather.common.converter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;
public class TokenConverter {

    private static String key;

    public static void setKey(String jwtKey) {
        key = jwtKey;
    }


    public static String getEmailByToken(String auth){

        String accessToken = auth.substring(7);

        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> claim = null;

        claim = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        return String.valueOf(claim.get("email"));
    }

    public static Map<String, Object> getMemberInfoFromAuth(String auth){

        String accessToken = auth.substring(7);

        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> claim = null;

        claim = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        return claim;
    }
}
