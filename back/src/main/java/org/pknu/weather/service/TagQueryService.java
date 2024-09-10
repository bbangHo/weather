package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.tag.HumidityTag;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.dto.TagDto;
import org.pknu.weather.dto.TagQueryResult;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagQueryService {
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;

    /**
     * 간단 날씨 보기 기능에서 태그 정보를 불러온다.
     *
     * @param memberId
     * @return
     */
    public List<TagDto.SimpleTag> getMostSelectedTags(Long memberId) {
        Member member = memberRepository.safeFindById(memberId);
        Location location = member.getLocation();

        List<TagQueryResult> tagQueryResultList = tagRepository.rankingTags(location)
                .stream()
                .sorted((o1, o2) -> Math.toIntExact(o1.getCount() - o2.getCount()))
                .toList();

        List<TagQueryResult> subList = tagQueryResultList.subList(0, 3);

        for (TagQueryResult tagQueryResult : subList) {
            // 온도, 습도 태그가 1~3 순위에 못 들어가는 경우
            if (tagQueryResult.getTag().equals(TemperatureTag.class) || tagQueryResult.getTag().equals(HumidityTag.class)) {
                subList.remove(tagQueryResult);
            }
        }

//        if(subList.size() == 1) {
//
//        }

        return null;

    }
}
