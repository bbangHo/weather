package org.pknu.weather.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.apiPayload.code.status.SuccessStatus;
import org.pknu.weather.common.GlobalParams;
import org.pknu.weather.service.MemberQueryService;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Map;


@Aspect
@Component
@RequiredArgsConstructor
public class MainPageControllerAspect {
    private final MemberQueryService memberQueryService;

    /**
     * 사용자가 지역을 등록했는지 확인하는 공통 로직
     *
     * @param pjp
     * @param memberId TODO: email로 대체될 예정
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* org.pknu.weather.controller.MainPageControllerV1.*(..)) && args(memberId)",
            argNames = "pjp,memberId")
    public Object around(ProceedingJoinPoint pjp, Long memberId) throws Throwable {
        if (!memberQueryService.hasRegisteredLocation(memberId)) {
            String address = "http://" + InetAddress.getLocalHost().getHostAddress();
            Map<String, String> result = Map.of("url", address + GlobalParams.LOCATION_REDIRECT_URL);
            return ApiResponse.of(SuccessStatus._REDIRECT, SuccessStatus._REDIRECT.getMessage(), result);
        }

        return pjp.proceed();
    }
}
