package org.pknu.weather.dto;


import lombok.*;
import org.pknu.weather.domain.common.Sensitivity;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDTO {

    private String email;

    private Sensitivity sensitivity;

    private String nickname;

    private String profileImage;

}
