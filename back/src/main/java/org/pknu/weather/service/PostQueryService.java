package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public void getPosts(Long memberId, Long LastPostId) {
        Member member = memberRepository.safeFindById(memberId);
        Location location = member.getLocation();

        // 인접 동네를 구하는 로직


    }
}
