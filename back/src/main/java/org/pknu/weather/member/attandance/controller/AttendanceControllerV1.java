package org.pknu.weather.member.attandance.controller;

import static org.pknu.weather.common.converter.TokenConverter.getEmailByToken;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.apipayload.ApiResponse;
import org.pknu.weather.member.attandance.service.AttendanceDbProcessor;
import org.pknu.weather.member.attandance.service.AttendanceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AttendanceControllerV1 {

    private final AttendanceService attendanceService;
    private final AttendanceDbProcessor attendanceDbProcessor;

    @PostMapping("/api/v1/attendance/check-in")
    public ApiResponse<Boolean> checkIn(@RequestHeader("Authorization") String authorization) {
        attendanceDbProcessor.processCheckInDbLogic(getEmailByToken(authorization));
        return ApiResponse.onSuccess();
    }

    @PostMapping("/api/v2/attendance/check-in")
    public ApiResponse<Boolean> checkInV2(@RequestHeader("Authorization") String authorization) {
        attendanceService.checkInV2(getEmailByToken(authorization));
        return ApiResponse.onSuccess();
    }
}
