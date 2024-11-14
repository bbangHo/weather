//package org.pknu.weather.aop;
//
//import java.net.InetAddress;
//import java.util.Map;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.junit.jupiter.api.Assertions;
//import org.pknu.weather.apiPayload.ApiResponse;
//import org.pknu.weather.apiPayload.code.status.SuccessStatus;
//import org.pknu.weather.common.GlobalParams;
//import org.pknu.weather.controller.MainPageControllerV1;
//import org.pknu.weather.domain.Member;
//import org.pknu.weather.repository.MemberRepository;
//import org.pknu.weather.service.MemberQueryService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestComponent;
//
//@SpringBootTest
//@Slf4j
//class AspectTest {
//
//    @Autowired
//    MainPageControllerV1 mainPageControllerV1;
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Autowired
//    TestLocationCheckAspect testLocationCheckAspect;
//
//    public void aopTest() {
//        // given
//        Member member = Member.builder().build();
//        Member save = memberRepository.save(member);
//
//        // when
//        ApiResponse<String> result = mainPageControllerV1.aopTest(member.getEmail());
//
//        // then
//        Assertions.assertEquals("303", result.getCode());
//    }
//
//    @Aspect
//    @TestComponent
//    @RequiredArgsConstructor
//    public class TestLocationCheckAspect {
//        private final MemberQueryService memberQueryService;
//
//        @Around("execution(* org.pknu.weather.aop.AspectTest.*())")
//        public Object locationCheck(ProceedingJoinPoint pjp) throws Throwable {
//            if (!memberQueryService.hasRegisteredLocation()) {
//                String address = "http://" + InetAddress.getLocalHost().getHostAddress();
//                Map<String, String> result = Map.of("url", address + GlobalParams.LOCATION_REDIRECT_URL);
//                return ApiResponse.of(SuccessStatus._REDIRECT, SuccessStatus._REDIRECT.getMessage(), result);
//            }
//
//            return pjp.proceed();
//        }
//    }
//
//
//}