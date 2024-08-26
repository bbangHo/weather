package org.pknu.weather.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class LoginMemberDTO {

    private Long id;
    private String email;

}
