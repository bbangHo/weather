package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {
    private final MemberRepository memberRepository;

    /**
     * 사용자가 location을 등록했는지 확인하는 메서드
     *
     * @param email 사용자 email
     * @return true = 등록되어있음, false = 등록되지 않았음
     */
    public Boolean hasRegisteredLocation(String email) {
        return memberRepository.hasRegisteredLocation(email);
    }
}
