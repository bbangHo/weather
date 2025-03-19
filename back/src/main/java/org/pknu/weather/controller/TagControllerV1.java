package org.pknu.weather.controller;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.common.mapper.EnumTagMapper;
import org.pknu.weather.dto.TagDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TagControllerV1 {
    private final EnumTagMapper enumTagMapper;

    @GetMapping("/tags")
    public ApiResponse<Map<String, List<TagDto>>> getTagList() {
        Map<String, List<TagDto>> tagMap = enumTagMapper.getAllDto();
        return ApiResponse.onSuccess(tagMap);
    }
}
