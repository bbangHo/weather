package org.pknu.weather.dto;


import lombok.*;
import org.pknu.weather.domain.common.Sensitivity;


public class MemberResponse {

    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberResponseDTO {

        private String email;

        private Sensitivity sensitivity;

        private String nickname;

        private String profileImage;

    }
    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberResponseWithAddressDTO {

        private String email;

        private Sensitivity sensitivity;

        private String nickname;

        private String profileImage;

        private Long locationId;

        private String location;
    }

}
