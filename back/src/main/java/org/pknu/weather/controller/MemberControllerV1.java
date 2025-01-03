package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.dto.MemberJoinDTO;
import org.pknu.weather.dto.MemberResponse;
import org.pknu.weather.dto.TermsDto;
import org.pknu.weather.service.MemberService;
import org.pknu.weather.service.WeatherService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;
import static org.pknu.weather.common.converter.TokenConverter.getMemberInfoFromAuth;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberControllerV1 {

    private final MemberService memberService;
    private final WeatherService weatherService;

    @PostMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MemberResponse.MemberResponseDTO> saveMemberInfo(
            @RequestHeader("Authorization") String authorization,
            MemberJoinDTO memberJoinDTO,
            @ModelAttribute("termsDto") TermsDto termsDto) {
        log.debug("/api/v1/member controller start ............");

        String email = getEmailByToken(authorization);

        MemberResponse.MemberResponseDTO memberResponseDTO = memberService.checkNicknameAndSave(email, memberJoinDTO, termsDto);

        return ApiResponse.onSuccess(memberResponseDTO);
    }

    @GetMapping(value = "/info")
    public ApiResponse<MemberResponse.MemberResponseWithAddressDTO> getMemberInfo(
            @RequestHeader("Authorization") String authorization) {

        String email = getEmailByToken(authorization);

        MemberResponse.MemberResponseWithAddressDTO fullMemberInfo = memberService.findFullMemberInfoByEmail(email);

        return ApiResponse.onSuccess(fullMemberInfo);
    }

    @DeleteMapping
    public ApiResponse<Object> deleteMember(@RequestHeader("Authorization") String authorization) {

        Map<String, Object> memberInfo = getMemberInfoFromAuth(authorization);
        memberService.deleteMember(memberInfo);

        return ApiResponse.onSuccess();
    }
}
