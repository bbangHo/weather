package org.pknu.weather.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.dto.MemberResponse;

@Slf4j
public class MemberResponseConverter {

    public static MemberResponse.MemberResponseDTO toMemberResponseDTO(Member member) {
        return MemberResponse.MemberResponseDTO.builder()
                .email(member.getEmail())
                .sensitivity(member.getSensitivity())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .build();
    }

    public static MemberResponse.MemberResponseWithAddressDTO toMemberResponseWithAddressDTO(Member member) {
        return MemberResponse.MemberResponseWithAddressDTO.builder()
                .location(member.getLocation().getFullAddress())
                .locationId(member.getLocation().getId())
                .email(member.getEmail())
                .sensitivity(member.getSensitivity())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .build();
    }
}
