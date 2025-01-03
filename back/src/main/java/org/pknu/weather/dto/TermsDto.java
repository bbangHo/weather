package org.pknu.weather.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermsDto {
    private Boolean isServiceTermsAgreed;
    private Boolean isPrivacyPolicyAgreed;
    private Boolean isLocationServiceTermsAgreed;
    private Boolean isPushNotificationAgreed;
}
