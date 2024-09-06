package org.pknu.weather.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.common.utils.LocalUploaderUtils;
import org.pknu.weather.common.utils.S3UploaderUtils;
import org.pknu.weather.domain.Member;
import org.pknu.weather.dto.LocationDTO;
import org.pknu.weather.dto.MemberJoinDTO;
import org.pknu.weather.dto.MemberResponseDTO;
import org.pknu.weather.service.MemberService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

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

        MemberResponseDTO memberResponseDTO = memberService.saveMemberInfo(email, memberJoinDTO);

        return ApiResponse.onSuccess(memberResponseDTO);
    }
}
