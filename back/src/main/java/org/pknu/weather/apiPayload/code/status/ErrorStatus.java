package org.pknu.weather.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pknu.weather.apiPayload.code.BaseErrorCode;
import org.pknu.weather.apiPayload.code.ErrorReasonDTO;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러, 관리자에게 문의 바랍니다."),

    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON_400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON_401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "금지된 요청입니다."),




    //Token
    ACCESS_TOKEN_NOT_ACCEPTED(HttpStatus.UNAUTHORIZED, "Jwt_400_1", "Access Token이 존재하지 않습니다."),
    ACCESS_TOKEN_BADTYPE(HttpStatus.UNAUTHORIZED, "Jwt_400_2", "Access Token의 타입이 bearer가 아닙니다."),
    MALFORMED_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "JWT_400_3", "Access Token의 값이 올바르게 설정되지 않았습니다. "),
    BAD_SIGNED_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "JWT_400_4", "Access Token의 서명이 올바르지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "JWT_400_5", "Access Token이 만료되었습니다."),
    MALFORMED_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "JWT_400_7", "Refresh Token의 값이 올바르게 설정되지 않았습니다. "),
    EXPIRED_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "JWT_400_8", "Refresh Token이 만료되었습니다."),
    TOKENS_NOT_ACCEPTED(HttpStatus.UNAUTHORIZED, "Jwt_400_9", "Access Token과 refresh Token이 존재하지 않습니다."),
    //Login
    ID_AND_PASSWORD_NOT_MATCH(HttpStatus.FORBIDDEN,"LOGIN_400_1","ID 또는 비밀번호가 잘못되었습니다."),
    LOGIN_DATA_NOT_ACCEPTED(HttpStatus.UNAUTHORIZED,"LOGIN_400_2","로그인 ID와 비밀번호가 비어있습니다."),
    MALFORMED_LOGIN_DATA(HttpStatus.UNAUTHORIZED,"LOGIN_400_3","로그인 ID와 비밀번호을 전달하는 JSON 형식에 문제가 있습니다."),

    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .message(message)
                .code(code)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .message(message)
                .code(code)
                .httpStatus(httpStatus)
                .build();
    }
}
