package org.pknu.weather.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.security.dto.MemberSecurityDTO;
import org.pknu.weather.security.util.JWTUtil;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException{
        log.info("Login Success Handler...................");


        MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO)authentication.getPrincipal();


        Map<String, Object> claims = Map.of("loginId",authentication.getName(),"id",memberSecurityDTO.getId());


        //access Token의 유효기간을 하루로 설정
        String accessToken = jwtUtil.generateToken(claims,1);
        //refresh Token의 유효기간 30일
        String refreshToken = jwtUtil.generateToken(claims,30);

        Map<String, String> tokens = Map.of("accessToken", accessToken,"refreshToken", refreshToken);

        Gson gson = new Gson();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        String responseStr = gson.toJson(ApiResponse.onSuccess(tokens));

        response.getWriter().println(responseStr);
    }
}
