package org.pknu.weather.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.dto.LocationDTO;
import org.pknu.weather.dto.MemberResponseDTO;

@Slf4j
public class MemberResponseConverter {

    public static MemberResponseDTO toMemberResponseDTO(Member member) {
        return MemberResponseDTO.builder()
                .email(member.getEmail())
                .sensitivity(member.getSensitivity())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .build();
    }
}
