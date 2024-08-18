package org.pknu.weather.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
@ToString
public class MemberSecurityDTO extends User {

    private Long id;
    private String email;

    public MemberSecurityDTO(Long id, String email, Collection<GrantedAuthority> authorities) {
        super(email, "" ,authorities);
        this.id = id;
        this.email = email;
    }



}
