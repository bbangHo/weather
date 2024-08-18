package org.pknu.weather.security.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Member;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.security.dto.MemberSecurityDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> result = memberRepository.findMemberByEmail(username);

        Member member = result.orElseThrow(() -> new UsernameNotFoundException("MEMBER_NOT_FOUND"));
        log.info("MemberDetailsService Member-------------------");

        // 권한 리스트 생성
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));  // 임시로 ROLE_USER 권한 추가

        return new MemberSecurityDTO(
                member.getId(),
                member.getEmail(),
                authorities);
    }
}
