package org.pknu.weather.config;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.filter.OauthTokenFilter;
import org.pknu.weather.filter.RefreshTokenFilter;
import org.pknu.weather.filter.TokenCheckFilter;
import org.pknu.weather.filter.TokenGenerateFilter;
import org.pknu.weather.feignClient.KaKaoLoginClient;
import org.pknu.weather.security.util.JWTUtil;
import org.pknu.weather.service.MemberService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final KaKaoLoginClient kaKaoLoginClient;
    private final MemberService memberservice;
    private final JWTUtil jwtUtil;

    @Bean
    public FilterRegistrationBean<RefreshTokenFilter> refreshTokenFilterRegister() {
        FilterRegistrationBean<RefreshTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RefreshTokenFilter(jwtUtil));
        registrationBean.addUrlPatterns("/refreshToken");
        registrationBean.setOrder(1);
        return registrationBean;
    }


    @Bean
    public FilterRegistrationBean<OauthTokenFilter> oauthTokenCheckFilterRegister() {
        FilterRegistrationBean<OauthTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new OauthTokenFilter(kaKaoLoginClient));
        registrationBean.addUrlPatterns("/token");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<TokenGenerateFilter> tokenGenerateFilterRegister() {
        FilterRegistrationBean<TokenGenerateFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenGenerateFilter(memberservice,jwtUtil));
        registrationBean.addUrlPatterns("/token");
        registrationBean.setOrder(3);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<TokenCheckFilter> tokenCheckFilterRegister() {
        FilterRegistrationBean<TokenCheckFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenCheckFilter(jwtUtil));
        registrationBean.setOrder(4);
        return registrationBean;
    }

}

