package org.pknu.weather.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.Member;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.security.exception.TokenException;
import org.pknu.weather.security.util.JWTUtil;
import org.pknu.weather.service.MemberService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


         log.info("Token Check Filter............................");

        try{
            validateAccessToken(request);

            filterChain.doFilter(request,response);

        }catch (TokenException TokenException){
            TokenException.sendResponseError(response);
        }
    }

    private Map<String, Object> validateAccessToken(HttpServletRequest request) throws TokenException{
        String headerStr = request.getHeader("Authorization");
        if(headerStr == null || headerStr.length() < 8){
            throw new TokenException(ErrorStatus.ACCESS_TOKEN_NOT_ACCEPTED);
        }

        String tokenType = headerStr.substring(0,6);
        String tokenStr = headerStr.substring(7);

        if(!tokenType.equalsIgnoreCase("Bearer")){
            log.error("BadType error..................");
            throw new TokenException(ErrorStatus.ACCESS_TOKEN_BADTYPE);
        }

        try {
            return jwtUtil.validateToken(tokenStr);
        }catch(MalformedJwtException malformedJwtException){
            log.error("MalformedJwtException.................");
            throw new TokenException(ErrorStatus.MALFORMED_ACCESS_TOKEN);
        }catch(SignatureException signatureException){
            log.error("SignatureException.................");
            throw new TokenException(ErrorStatus.BAD_SIGNED_ACCESS_TOKEN);
        }catch(ExpiredJwtException expiredJwtException){
            log.error("ExpiredJwtException.................");
            throw new TokenException(ErrorStatus.EXPIRED_ACCESS_TOKEN);
        }
    }
}
