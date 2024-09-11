package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.dto.MemberJoinDTO;
import org.pknu.weather.dto.MemberResponseDTO;
import org.pknu.weather.service.MemberService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberControllerV1 {

    private final MemberService memberService;

    @PostMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MemberResponseDTO> saveMemberInfo(@RequestHeader("Authorization") String authorization,
                                                   MemberJoinDTO memberJoinDTO) {
        log.debug("/api/v1/member controller start ............");

        String email = getEmailByToken(authorization);

        MemberResponseDTO memberResponseDTO = memberService.checkNicknameAndSave(email, memberJoinDTO);

        return ApiResponse.onSuccess(memberResponseDTO);
    }
}
