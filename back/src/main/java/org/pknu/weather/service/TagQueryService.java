package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.dto.TagQueryResult;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagQueryService {
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;

    public void getMostTags(Long memberId) {
        Member member = memberRepository.safeFindById(memberId);
        Location location = member.getLocation();
//        TagQueryResult tagQueryResult = tagRepository.rankingTags(location);
    }
}
